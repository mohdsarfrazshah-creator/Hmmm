package com.example.network

import android.util.Log
import com.example.model.CategorySummary
import com.example.model.ContentCategory
import com.example.model.ContentItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.security.MessageDigest
import kotlin.random.Random

class FlowSocketEngine {

  private val TAG = "FlowSocketEngine"
  private var serverSocket: ServerSocket? = null
  private var clientSocket: Socket? = null
  @Volatile private var isRunning = false

  // Listener for transfer updates
  interface TransferProgressListener {
    fun onTransferProgress(
      transferredBytes: Long,
      totalBytes: Long,
      speedMbps: Double,
      currentFileName: String,
      currentCategory: ContentCategory
    )
    fun onFileCompleted(fileName: String, bytes: Long)
    fun onTransferCompleted(totalBytes: Long, totalFiles: Int, elapsedSeconds: Long)
    fun onTransferFailed(error: String)
  }

  suspend fun startServer(
    port: Int = FlowTransmissionProtocol.DEFAULT_PORT,
    sessionPin: String,
    listener: TransferProgressListener
  ) = withContext(Dispatchers.IO) {
    try {
      serverSocket = ServerSocket(port)
      isRunning = true
      Log.d(TAG, "FlowTransfer Server started on port $port")

      val socket = serverSocket?.accept() ?: return@withContext
      val crypto = FlowTransmissionProtocol.CryptoEngine(
        FlowTransmissionProtocol.CryptoEngine.generateSessionKey(sessionPin)
      )

      val inStream = DataInputStream(BufferedInputStream(socket.getInputStream()))
      val outStream = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))

      // 1. Handshake Packet
      val (headerType, payloadLen) = FlowTransmissionProtocol.readPacketHeader(inStream)
      if (headerType == FlowTransmissionProtocol.PacketType.HANDSHAKE) {
        val payload = ByteArray(payloadLen)
        inStream.readFully(payload)
        val decrypted = crypto.decrypt(payload)
        val handshakeMsg = String(decrypted, Charsets.UTF_8)
        Log.d(TAG, "Handshake received: $handshakeMsg")

        // Respond with HANDSHAKE_ACK
        val ackBytes = crypto.encrypt("FLOW_HANDSHAKE_OK".toByteArray(Charsets.UTF_8))
        FlowTransmissionProtocol.writePacketHeader(
          outStream,
          FlowTransmissionProtocol.PacketType.HANDSHAKE_ACK,
          ackBytes.size
        )
        outStream.write(ackBytes)
        outStream.flush()
      }

      // 2. Stream Data Packets
      var totalReceivedBytes = 0L
      var totalFilesCount = 0
      val startTime = System.currentTimeMillis()

      while (isRunning && !socket.isClosed) {
        try {
          val (pType, pLen) = FlowTransmissionProtocol.readPacketHeader(inStream)
          when (pType) {
            FlowTransmissionProtocol.PacketType.FILE_HEADER -> {
              val headerPayload = ByteArray(pLen)
              inStream.readFully(headerPayload)
              val metaStr = String(crypto.decrypt(headerPayload), Charsets.UTF_8)
              // format: fileName:size:category
              val parts = metaStr.split(":")
              val fileName = parts.getOrNull(0) ?: "unknown_file"
              val fileSize = parts.getOrNull(1)?.toLongOrNull() ?: 0L
              val categoryStr = parts.getOrNull(2) ?: "PHOTOS"
              val category = ContentCategory.entries.firstOrNull { it.name == categoryStr } ?: ContentCategory.PHOTOS

              listener.onTransferProgress(
                totalReceivedBytes,
                totalReceivedBytes + fileSize,
                78.0,
                fileName,
                category
              )
            }

            FlowTransmissionProtocol.PacketType.FILE_CHUNK -> {
              val chunkPayload = ByteArray(pLen)
              inStream.readFully(chunkPayload)
              val rawChunk = crypto.decrypt(chunkPayload)
              totalReceivedBytes += rawChunk.size

              val elapsedSec = ((System.currentTimeMillis() - startTime) / 1000.0).coerceAtLeast(0.1)
              val speed = (totalReceivedBytes.toDouble() / (1024.0 * 1024.0)) / elapsedSec

              listener.onTransferProgress(
                totalReceivedBytes,
                totalReceivedBytes,
                speed,
                "Receiving packets...",
                ContentCategory.PHOTOS
              )
            }

            FlowTransmissionProtocol.PacketType.FILE_ACK -> {
              totalFilesCount++
            }

            FlowTransmissionProtocol.PacketType.COMPLETION -> {
              val duration = ((System.currentTimeMillis() - startTime) / 1000).coerceAtLeast(1)
              listener.onTransferCompleted(totalReceivedBytes, totalFilesCount, duration)
              break
            }

            else -> {
              inStream.skipBytes(pLen)
            }
          }
        } catch (e: Exception) {
          Log.w(TAG, "Socket read ended or completed: ${e.message}")
          break
        }
      }

      socket.close()
    } catch (e: Exception) {
      Log.e(TAG, "Server error: ${e.message}")
      listener.onTransferFailed(e.message ?: "Server transmission error")
    } finally {
      stop()
    }
  }

  suspend fun startClient(
    targetHost: String,
    port: Int = FlowTransmissionProtocol.DEFAULT_PORT,
    sessionPin: String,
    categories: List<CategorySummary>,
    listener: TransferProgressListener
  ) = withContext(Dispatchers.IO) {
    try {
      clientSocket = Socket()
      clientSocket?.connect(InetSocketAddress(targetHost, port), 5000)
      isRunning = true

      val crypto = FlowTransmissionProtocol.CryptoEngine(
        FlowTransmissionProtocol.CryptoEngine.generateSessionKey(sessionPin)
      )

      val socket = clientSocket ?: return@withContext
      val outStream = DataOutputStream(BufferedOutputStream(socket.getOutputStream()))
      val inStream = DataInputStream(BufferedInputStream(socket.getInputStream()))

      // 1. Send Handshake
      val handshakePayload = crypto.encrypt("FLOW_CLIENT_HELLO:v2".toByteArray(Charsets.UTF_8))
      FlowTransmissionProtocol.writePacketHeader(
        outStream,
        FlowTransmissionProtocol.PacketType.HANDSHAKE,
        handshakePayload.size
      )
      outStream.write(handshakePayload)
      outStream.flush()

      // Read Handshake ACK
      val (ackType, ackLen) = FlowTransmissionProtocol.readPacketHeader(inStream)
      if (ackType != FlowTransmissionProtocol.PacketType.HANDSHAKE_ACK) {
        throw IllegalStateException("Handshake failed. Protocol mismatch.")
      }
      val ackPayload = ByteArray(ackLen)
      inStream.readFully(ackPayload)

      // 2. Stream Selected Items
      val selectedCats = categories.filter { it.isSelected }
      val totalBytes = selectedCats.sumOf { it.totalSizeBytes }
      val totalFiles = selectedCats.sumOf { it.itemCount }
      var transferredBytes = 0L
      var transferredFiles = 0
      val startTime = System.currentTimeMillis()

      for (cat in selectedCats) {
        for (item in cat.items) {
          if (!isRunning) break

          // Send File Header
          val meta = "${item.name}:${item.sizeBytes}:${item.category.name}"
          val headerEncrypted = crypto.encrypt(meta.toByteArray(Charsets.UTF_8))
          FlowTransmissionProtocol.writePacketHeader(
            outStream,
            FlowTransmissionProtocol.PacketType.FILE_HEADER,
            headerEncrypted.size
          )
          outStream.write(headerEncrypted)
          outStream.flush()

          // Send simulated 64KB file chunks with real crypto
          var fileOffset = 0L
          val dummyChunkBuffer = ByteArray(16 * 1024) { 0x42 } // 16 KB mock data
          while (fileOffset < item.sizeBytes && isRunning) {
            val remaining = item.sizeBytes - fileOffset
            val chunkSize = remaining.coerceAtMost(dummyChunkBuffer.size.toLong()).toInt()

            val chunkEncrypted = crypto.encrypt(dummyChunkBuffer.copyOf(chunkSize))
            FlowTransmissionProtocol.writePacketHeader(
              outStream,
              FlowTransmissionProtocol.PacketType.FILE_CHUNK,
              chunkEncrypted.size
            )
            outStream.write(chunkEncrypted)
            outStream.flush()

            fileOffset += chunkSize
            transferredBytes += chunkSize

            val elapsedSec = ((System.currentTimeMillis() - startTime) / 1000.0).coerceAtLeast(0.1)
            val speed = (transferredBytes.toDouble() / (1024.0 * 1024.0)) / elapsedSec

            listener.onTransferProgress(
              transferredBytes,
              totalBytes,
              speed,
              item.name,
              item.category
            )
          }

          transferredFiles++
          // Send FILE_ACK
          FlowTransmissionProtocol.writePacketHeader(
            outStream,
            FlowTransmissionProtocol.PacketType.FILE_ACK,
            0
          )
          outStream.flush()
          listener.onFileCompleted(item.name, item.sizeBytes)
        }
      }

      // 3. Send Completion
      FlowTransmissionProtocol.writePacketHeader(
        outStream,
        FlowTransmissionProtocol.PacketType.COMPLETION,
        0
      )
      outStream.flush()

      val totalDuration = ((System.currentTimeMillis() - startTime) / 1000).coerceAtLeast(1)
      listener.onTransferCompleted(transferredBytes, totalFiles, totalDuration)
    } catch (e: Exception) {
      Log.e(TAG, "Client error: ${e.message}")
      listener.onTransferFailed(e.message ?: "Client transmission error")
    } finally {
      stop()
    }
  }

  fun stop() {
    isRunning = false
    try {
      serverSocket?.close()
      clientSocket?.close()
    } catch (e: Exception) {
      // Ignored
    }
    serverSocket = null
    clientSocket = null
  }
}
