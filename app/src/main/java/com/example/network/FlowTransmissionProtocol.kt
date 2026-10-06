package com.example.network

import java.io.DataInputStream
import java.io.DataOutputStream
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object FlowTransmissionProtocol {
  const val DEFAULT_PORT = 8988
  const val MAGIC_HEADER = 0x464C4F57 // "FLOW" in ASCII
  const val PROTOCOL_VERSION = 2
  const val CHUNK_BUFFER_SIZE = 64 * 1024 // 64 KB high-speed streaming chunk

  enum class PacketType(val code: Byte) {
    HANDSHAKE(0x01),
    HANDSHAKE_ACK(0x02),
    MANIFEST(0x03),
    FILE_HEADER(0x04),
    FILE_CHUNK(0x05),
    FILE_ACK(0x06),
    RESUME_REQUEST(0x07),
    COMPLETION(0x08),
    PING(0x09)
  }

  // Cryptographic Helper (AES-GCM-256)
  class CryptoEngine(secretKeyBytes: ByteArray) {
    private val secretKey: SecretKey = SecretKeySpec(secretKeyBytes, "AES")
    private val random = SecureRandom()

    fun encrypt(plainBytes: ByteArray): ByteArray {
      val iv = ByteArray(12)
      random.nextBytes(iv)
      val cipher = Cipher.getInstance("AES/GCM/NoPadding")
      val spec = GCMParameterSpec(128, iv)
      cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
      val cipherText = cipher.doFinal(plainBytes)

      // Prepend IV to ciphertext
      val result = ByteArray(iv.size + cipherText.size)
      System.arraycopy(iv, 0, result, 0, iv.size)
      System.arraycopy(cipherText, 0, result, iv.size, cipherText.size)
      return result
    }

    fun decrypt(encryptedBytes: ByteArray): ByteArray {
      if (encryptedBytes.size < 12) throw IllegalArgumentException("Ciphertext too short for IV")
      val iv = ByteArray(12)
      System.arraycopy(encryptedBytes, 0, iv, 0, 12)
      val cipherTextSize = encryptedBytes.size - 12
      val cipherText = ByteArray(cipherTextSize)
      System.arraycopy(encryptedBytes, 12, cipherText, 0, cipherTextSize)

      val cipher = Cipher.getInstance("AES/GCM/NoPadding")
      val spec = GCMParameterSpec(128, iv)
      cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
      return cipher.doFinal(cipherText)
    }

    companion object {
      fun generateSessionKey(pin: String): ByteArray {
        val digest = MessageDigest.getInstance("SHA-256")
        val salt = "FlowTransfer-P2P-Salt-2026".toByteArray(Charsets.UTF_8)
        digest.update(salt)
        return digest.digest(pin.toByteArray(Charsets.UTF_8))
      }
    }
  }

  fun writePacketHeader(out: DataOutputStream, type: PacketType, payloadLength: Int) {
    out.writeInt(MAGIC_HEADER)
    out.writeByte(PROTOCOL_VERSION)
    out.writeByte(type.code.toInt())
    out.writeInt(payloadLength)
  }

  fun readPacketHeader(inStream: DataInputStream): Pair<PacketType, Int> {
    val magic = inStream.readInt()
    if (magic != MAGIC_HEADER) {
      throw IllegalStateException("Invalid FlowTransfer magic header: 0x${Integer.toHexString(magic)}")
    }
    val version = inStream.readByte()
    val typeByte = inStream.readByte()
    val length = inStream.readInt()

    val type = PacketType.entries.firstOrNull { it.code == typeByte }
      ?: throw IllegalStateException("Unknown packet type: $typeByte")
    return Pair(type, length)
  }
}
