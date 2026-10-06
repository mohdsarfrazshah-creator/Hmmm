package com.example

import com.example.data.TransferRepository
import com.example.model.ActiveTransferState
import com.example.model.ConnectionType
import com.example.model.ContentCategory
import com.example.model.DeviceType
import com.example.model.TransferStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testContentCategoriesAreComplete() {
    val categories = ContentCategory.entries
    assertEquals(10, categories.size)
    assertTrue(categories.contains(ContentCategory.PHOTOS))
    assertTrue(categories.contains(ContentCategory.VIDEOS))
    assertTrue(categories.contains(ContentCategory.CONTACTS))
    assertTrue(categories.contains(ContentCategory.DOCUMENTS))
    assertTrue(categories.contains(ContentCategory.AUDIO))
    assertTrue(categories.contains(ContentCategory.NOTES))
    assertTrue(categories.contains(ContentCategory.CALENDAR))
    assertTrue(categories.contains(ContentCategory.WALLPAPERS))
    assertTrue(categories.contains(ContentCategory.DOWNLOADS))
    assertTrue(categories.contains(ContentCategory.DEVICE_FILES))
  }

  @Test
  fun testByteFormatting() {
    assertEquals("0 B", TransferRepository.formatBytes(0))
    assertEquals("500 B", TransferRepository.formatBytes(500))
    assertEquals("1.0 KB", TransferRepository.formatBytes(1024))
    assertEquals("10.0 MB", TransferRepository.formatBytes(10 * 1024 * 1024))
    assertEquals("2.5 GB", TransferRepository.formatBytes((2.5 * 1024 * 1024 * 1024).toLong()))
  }

  @Test
  fun testActiveTransferProgressAndEta() {
    val state = ActiveTransferState(
      sessionId = "TEST1234",
      status = TransferStatus.TRANSFERRING,
      totalBytes = 100_000_000L, // 100 MB
      transferredBytes = 50_000_000L, // 50 MB
      currentSpeedMbps = 50.0 // 50 MB/s
    )

    assertEquals(0.5f, state.progressPercent, 0.001f)
    // 50 MB remaining at 50 MB/s = 1s remaining
    assertEquals(1L, state.remainingTimeSeconds)
  }

  @Test
  fun testConnectionTypes() {
    val direct = ConnectionType.WIFI_DIRECT
    assertEquals("Wi-Fi Direct P2P", direct.displayName)
    assertTrue(direct.avgSpeedMbps > 70.0)
  }

  @Test
  fun testFlowTransmissionCryptoAndHeaders() {
    val pin = "741289"
    val key = com.example.network.FlowTransmissionProtocol.CryptoEngine.generateSessionKey(pin)
    assertEquals(32, key.size) // 256-bit key

    val crypto = com.example.network.FlowTransmissionProtocol.CryptoEngine(key)
    val originalText = "FLOW_TRANSFER_PAYLOAD_TEST_2026"
    val encrypted = crypto.encrypt(originalText.toByteArray(Charsets.UTF_8))
    assertTrue(encrypted.size > originalText.length)

    val decryptedBytes = crypto.decrypt(encrypted)
    val decryptedText = String(decryptedBytes, Charsets.UTF_8)
    assertEquals(originalText, decryptedText)

    // Test Packet Header serialization
    val byteOut = java.io.ByteArrayOutputStream()
    val dataOut = java.io.DataOutputStream(byteOut)
    com.example.network.FlowTransmissionProtocol.writePacketHeader(
      dataOut,
      com.example.network.FlowTransmissionProtocol.PacketType.FILE_HEADER,
      256
    )
    dataOut.flush()

    val byteIn = java.io.ByteArrayInputStream(byteOut.toByteArray())
    val dataIn = java.io.DataInputStream(byteIn)
    val (packetType, length) = com.example.network.FlowTransmissionProtocol.readPacketHeader(dataIn)
    assertEquals(com.example.network.FlowTransmissionProtocol.PacketType.FILE_HEADER, packetType)
    assertEquals(256, length)
  }
}
