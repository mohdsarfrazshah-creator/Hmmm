package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.TransferRecordEntity
import com.example.data.TransferRepository
import com.example.model.ActiveTransferState
import com.example.model.AiOptimizationReport
import com.example.model.CategorySummary
import com.example.model.ConnectionType
import com.example.model.ContentCategory
import com.example.model.DevicePeer
import com.example.model.DeviceType
import com.example.model.TransferMode
import com.example.model.TransferStatus
import com.example.model.VerificationReport
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenState {
  SPLASH,
  HOME,
  DISCOVERY,
  DATA_SELECTION,
  ACTIVE_TRANSFER,
  COMPLETION_REPORT,
  PC_MAC_PORTAL,
  WIRELESS_BACKUP,
  TRANSFER_HISTORY,
  SETTINGS
}

data class FlowUiState(
  val currentScreen: ScreenState = ScreenState.SPLASH,
  val selectedTab: Int = 0, // 0: Home, 1: Backup, 2: History, 3: Settings
  val activeMode: TransferMode = TransferMode.SENDER,
  val selectedPeer: DevicePeer? = null,
  val isPairingDialogVisible: Boolean = false,
  val showQrScanner: Boolean = false,
  val isAiModalVisible: Boolean = false,
  val isOsLimitationSheetVisible: Boolean = false,
  val myDeviceName: String = "This Device (Pixel 9 Pro)",
  val myDeviceType: DeviceType = DeviceType.ANDROID,
  val myIpAddress: String = "192.168.49.1",
  val mySessionPin: String = "619420",
  val appLanguage: String = "en",
  val isDarkTheme: Boolean = true,
  val isE2eeEnabled: Boolean = true,
  val preferredConnection: ConnectionType = ConnectionType.WIFI_DIRECT,
  val backupInProgress: Boolean = false,
  val backupProgress: Float = 0f,
  val backupCompleted: Boolean = false
)

class FlowTransferViewModel(application: Application) : AndroidViewModel(application) {

  val repository = TransferRepository(application)

  private val _uiState = MutableStateFlow(FlowUiState())
  val uiState: StateFlow<FlowUiState> = _uiState.asStateFlow()

  val discoveredPeers: StateFlow<List<DevicePeer>> = repository.discoveredPeers
  val categories: StateFlow<List<CategorySummary>> = repository.categories
  val activeTransfer: StateFlow<ActiveTransferState> = repository.activeTransfer
  val aiReport: StateFlow<AiOptimizationReport?> = repository.aiReport
  val verificationReport: StateFlow<VerificationReport?> = repository.verificationReport

  val allTransfers: StateFlow<List<TransferRecordEntity>> = repository.allTransfers
    .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  val recentTransfers: StateFlow<List<TransferRecordEntity>> = repository.recentTransfers
    .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

  val completedCount: StateFlow<Int> = repository.completedTransfersCount
    .stateIn(viewModelScope, SharingStarted.Lazily, 0)

  val totalBytesTransferred: StateFlow<Long?> = repository.totalTransferredBytes
    .stateIn(viewModelScope, SharingStarted.Lazily, 0L)

  init {
    // Splash screen timer
    viewModelScope.launch {
      kotlinx.coroutines.delay(2100)
      if (_uiState.value.currentScreen == ScreenState.SPLASH) {
        _uiState.value = _uiState.value.copy(currentScreen = ScreenState.HOME)
      }
    }
  }

  fun navigateTo(screen: ScreenState) {
    _uiState.value = _uiState.value.copy(currentScreen = screen)
    if (screen == ScreenState.DISCOVERY) {
      repository.startDiscovery()
    } else {
      repository.stopDiscovery()
    }
  }

  fun setTab(index: Int) {
    _uiState.value = _uiState.value.copy(selectedTab = index)
    when (index) {
      0 -> _uiState.value = _uiState.value.copy(currentScreen = ScreenState.HOME)
      1 -> _uiState.value = _uiState.value.copy(currentScreen = ScreenState.WIRELESS_BACKUP)
      2 -> _uiState.value = _uiState.value.copy(currentScreen = ScreenState.TRANSFER_HISTORY)
      3 -> _uiState.value = _uiState.value.copy(currentScreen = ScreenState.SETTINGS)
    }
  }

  fun startTransferFlow(mode: TransferMode) {
    _uiState.value = _uiState.value.copy(
      activeMode = mode,
      currentScreen = ScreenState.DISCOVERY,
      selectedPeer = null,
      isPairingDialogVisible = false
    )
    repository.startDiscovery()
  }

  fun selectPeerForPairing(peer: DevicePeer) {
    _uiState.value = _uiState.value.copy(
      selectedPeer = peer,
      isPairingDialogVisible = true
    )
  }

  fun dismissPairingDialog() {
    _uiState.value = _uiState.value.copy(isPairingDialogVisible = false)
  }

  fun confirmPairing() {
    val peer = _uiState.value.selectedPeer ?: return
    _uiState.value = _uiState.value.copy(
      isPairingDialogVisible = false,
      currentScreen = if (_uiState.value.activeMode == TransferMode.SENDER) {
        ScreenState.DATA_SELECTION
      } else {
        // Receiver waits for sender to start
        ScreenState.ACTIVE_TRANSFER
      }
    )
    if (_uiState.value.activeMode == TransferMode.RECEIVER) {
      repository.startTransfer(peer, TransferMode.RECEIVER)
    }
  }

  fun toggleQrScanner(show: Boolean) {
    _uiState.value = _uiState.value.copy(showQrScanner = show)
  }

  fun onQrCodeScanned(qrPayload: String) {
    _uiState.value = _uiState.value.copy(showQrScanner = false)
    val pin = Regex("pin=([0-9A-Za-z]+)").find(qrPayload)?.groupValues?.get(1) ?: "842910"
    val device = Regex("device=([^&]+)").find(qrPayload)?.groupValues?.get(1)?.replace("+", " ") ?: "iPhone 16 Pro (QR Paired)"
    val ip = Regex("ip=([^&]+)").find(qrPayload)?.groupValues?.get(1) ?: "192.168.49.1"
    val isIphone = device.contains("iPhone", ignoreCase = true) || device.contains("iPad", ignoreCase = true)

    val scannedPeer = DevicePeer(
      id = "scanned-peer-${System.currentTimeMillis()}",
      name = device,
      deviceType = if (isIphone) DeviceType.IPHONE else DeviceType.ANDROID,
      connectionType = ConnectionType.WIFI_DIRECT,
      ipAddress = ip,
      sessionPin = pin,
      isPaired = true
    )
    selectPeerForPairing(scannedPeer)
  }

  fun toggleCategory(category: ContentCategory) {
    repository.toggleCategorySelection(category)
  }

  fun selectAllData(select: Boolean) {
    repository.selectAllCategories(select)
  }

  fun applySmartRecommendations() {
    repository.applySmartRecommendations()
  }

  fun runAiOptimization() {
    repository.runAiOptimizationAnalysis()
    _uiState.value = _uiState.value.copy(isAiModalVisible = true)
  }

  fun closeAiModal() {
    _uiState.value = _uiState.value.copy(isAiModalVisible = false)
  }

  fun applyAiCleanup() {
    repository.removeDuplicatesWithAi()
  }

  fun toggleOsLimitationSheet(show: Boolean) {
    _uiState.value = _uiState.value.copy(isOsLimitationSheetVisible = show)
  }

  fun startTransfer() {
    val peer = _uiState.value.selectedPeer ?: DevicePeer(
      id = "default-peer",
      name = "Target Device",
      deviceType = DeviceType.IPHONE
    )
    _uiState.value = _uiState.value.copy(currentScreen = ScreenState.ACTIVE_TRANSFER)
    repository.startTransfer(peer, _uiState.value.activeMode)
  }

  fun pauseTransfer() {
    repository.pauseTransfer()
  }

  fun resumeTransfer() {
    repository.resumeTransfer()
  }

  fun simulateDropAndResume() {
    repository.simulateConnectionDropAndAutoResume()
  }

  fun cancelTransfer() {
    repository.cancelTransfer()
    _uiState.value = _uiState.value.copy(currentScreen = ScreenState.HOME)
  }

  fun finishTransfer() {
    _uiState.value = _uiState.value.copy(currentScreen = ScreenState.COMPLETION_REPORT)
  }

  fun deleteHistoryRecord(id: Long) {
    viewModelScope.launch {
      repository.deleteHistoryRecord(id)
    }
  }

  fun clearHistory() {
    viewModelScope.launch {
      repository.clearAllHistory()
    }
  }

  fun startWirelessBackup() {
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(
        backupInProgress = true,
        backupProgress = 0f,
        backupCompleted = false
      )
      for (step in 1..20) {
        kotlinx.coroutines.delay(180)
        _uiState.value = _uiState.value.copy(backupProgress = step / 20f)
      }
      _uiState.value = _uiState.value.copy(
        backupInProgress = false,
        backupCompleted = true
      )
    }
  }

  fun setLanguage(lang: String) {
    _uiState.value = _uiState.value.copy(appLanguage = lang)
  }

  fun setDarkTheme(isDark: Boolean) {
    _uiState.value = _uiState.value.copy(isDarkTheme = isDark)
  }

  fun setE2eeEnabled(enabled: Boolean) {
    _uiState.value = _uiState.value.copy(isE2eeEnabled = enabled)
  }

  fun setPreferredConnection(connection: ConnectionType) {
    _uiState.value = _uiState.value.copy(preferredConnection = connection)
  }
}
