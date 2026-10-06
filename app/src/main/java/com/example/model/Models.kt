package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContactPage
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.ui.graphics.vector.ImageVector

enum class DeviceType(val displayName: String, val osFamily: String) {
  ANDROID("Android Phone", "Android"),
  IPHONE("iPhone", "iOS"),
  TABLET_ANDROID("Android Tablet", "Android"),
  IPAD("iPad", "iPadOS"),
  MAC("MacBook / Mac", "macOS"),
  WINDOWS_PC("Windows PC", "Windows")
}

enum class TransferMode {
  SENDER,
  RECEIVER
}

enum class ConnectionType(
  val displayName: String,
  val estimatedSpeed: String,
  val avgSpeedMbps: Double,
  val securityProtocol: String
) {
  WIFI_DIRECT("Wi-Fi Direct P2P", "~85 MB/s", 85.0, "WPA3 / AES-256"),
  LOCAL_WIFI("Local Wi-Fi Network", "~52 MB/s", 52.0, "TLS 1.3 / AES-256"),
  PERSONAL_HOTSPOT("Personal Hotspot", "~68 MB/s", 68.0, "WPA2-PSK / AES-256"),
  BLUETOOTH_BLE("Bluetooth Low Energy", "~2 MB/s", 2.0, "BLE 5.3 Pairing")
}

enum class ContentCategory(
  val displayName: String,
  val description: String,
  val icon: ImageVector,
  val iosSupported: Boolean,
  val note: String
) {
  PHOTOS(
    "Photos",
    "Camera Roll, Albums, Screenshots & RAW images",
    Icons.Default.Image,
    true,
    "Full metadata, EXIF, and albums preserved"
  ),
  VIDEOS(
    "Videos",
    "Recorded 4K, 1080p clips, movies & slow-motion",
    Icons.Default.Videocam,
    true,
    "Lossless cross-platform H.264 / HEVC video playback"
  ),
  CONTACTS(
    "Contacts",
    "All phone numbers, emails, addresses & avatars",
    Icons.Default.ContactPage,
    true,
    "Exported as standardized vCard (v4.0) importable on all OS"
  ),
  DOCUMENTS(
    "Documents",
    "PDFs, Word docs, spreadsheets, slides & archives",
    Icons.Default.Description,
    true,
    "Transferred into device Files/Downloads storage"
  ),
  AUDIO(
    "Audio Files",
    "Music tracks, voice recordings & podcasts",
    Icons.Default.Audiotrack,
    true,
    "Standard MP3/AAC/M4A format recognized universally"
  ),
  DOWNLOADS(
    "Downloads",
    "Downloaded files, APKs, install packages & zip files",
    Icons.Default.Download,
    false,
    "iOS restricts direct download sandbox; saved to Files folder"
  ),
  NOTES(
    "Notes",
    "Personal memos, checklists & formatted texts",
    Icons.AutoMirrored.Filled.Note,
    true,
    "Standardized markdown/JSON format compatible everywhere"
  ),
  CALENDAR(
    "Calendar Events",
    "Scheduled meetings, birthdays & alarms",
    Icons.Default.CalendarMonth,
    true,
    "Universal iCalendar (.ics) format syncs with Apple & Google Calendar"
  ),
  WALLPAPERS(
    "Wallpapers",
    "Current lock screen and home screen backgrounds",
    Icons.Default.Wallpaper,
    true,
    "Saved in highest native resolution directly to gallery"
  ),
  DEVICE_FILES(
    "Device Files & Folders",
    "Custom folders, app documents & saved configurations",
    Icons.Default.Folder,
    true,
    "System app data sandbox is OS-restricted; user files full access"
  )
}

data class ContentItem(
  val id: String,
  val name: String,
  val category: ContentCategory,
  val sizeBytes: Long,
  val formattedSize: String,
  val isRecommendedByAi: Boolean = false,
  val isDuplicate: Boolean = false,
  val duplicateOriginalId: String? = null
)

data class CategorySummary(
  val category: ContentCategory,
  val itemCount: Int,
  val totalSizeBytes: Long,
  val formattedSize: String,
  val isSelected: Boolean = true,
  val items: List<ContentItem> = emptyList()
)

data class DevicePeer(
  val id: String,
  val name: String,
  val deviceType: DeviceType,
  val connectionType: ConnectionType = ConnectionType.WIFI_DIRECT,
  val signalDbm: Int = -42,
  val ipAddress: String = "192.168.49.1",
  val port: Int = 8988,
  val sessionPin: String = "482915",
  val isPaired: Boolean = false,
  val isTrusted: Boolean = false
)

enum class TransferStatus {
  IDLE,
  CONNECTING,
  ANALYZING_AI,
  READY_TO_TRANSFER,
  TRANSFERRING,
  PAUSED,
  RECONNECTING,
  VERIFYING,
  COMPLETED,
  FAILED
}

data class ActiveTransferState(
  val sessionId: String = "",
  val status: TransferStatus = TransferStatus.IDLE,
  val peerDevice: DevicePeer? = null,
  val transferMode: TransferMode = TransferMode.SENDER,
  val connectionType: ConnectionType = ConnectionType.WIFI_DIRECT,
  val totalFiles: Int = 0,
  val transferredFiles: Int = 0,
  val totalBytes: Long = 0L,
  val transferredBytes: Long = 0L,
  val currentSpeedMbps: Double = 0.0,
  val currentFileName: String = "",
  val currentCategory: ContentCategory = ContentCategory.PHOTOS,
  val isEncryptionActive: Boolean = true,
  val encryptionCipher: String = "AES-GCM-256",
  val isReconnecting: Boolean = false,
  val resumeFileIndex: Int = 0,
  val elapsedTimeSeconds: Long = 0L
) {
  val progressPercent: Float
    get() = if (totalBytes > 0) (transferredBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f) else 0f

  val remainingTimeSeconds: Long
    get() {
      if (currentSpeedMbps <= 0.0) return 0L
      val remainingBytes = (totalBytes - transferredBytes).coerceAtLeast(0L)
      if (remainingBytes == 0L) return 0L
      val bytesPerSecond = currentSpeedMbps * 1024 * 1024
      return if (bytesPerSecond > 0) kotlin.math.ceil(remainingBytes.toDouble() / bytesPerSecond).toLong() else 0L
    }
}

data class AiOptimizationReport(
  val totalFilesAnalyzed: Int,
  val totalOriginalSizeBytes: Long,
  val duplicatesFoundCount: Int,
  val duplicateBytesSaved: Long,
  val largeMediaFoldersFound: List<String>,
  val suggestedCleanupsCount: Int,
  val aiRecommendationNote: String
)

data class VerificationReport(
  val sessionId: String,
  val totalFilesTarget: Int,
  val totalFilesReceived: Int,
  val totalBytesTarget: Long,
  val totalBytesReceived: Long,
  val missingFilesCount: Int,
  val sha256VerificationPassed: Boolean,
  val checksumValidPercent: Double,
  val durationSeconds: Long,
  val avgSpeedMbps: Double,
  val categoryBreakdown: Map<ContentCategory, Pair<Int, Long>>
)
