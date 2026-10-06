package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.ActiveTransferScreen
import com.example.ui.screens.CompletionScreen
import com.example.ui.screens.DataSelectionScreen
import com.example.ui.screens.DeviceDiscoveryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PcMacPortalScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TransferHistoryScreen
import com.example.ui.screens.WirelessBackupScreen
import com.example.ui.theme.FlowTransferTheme
import com.example.viewmodel.FlowTransferViewModel
import com.example.viewmodel.ScreenState

class MainActivity : ComponentActivity() {

  private val viewModel: FlowTransferViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val uiState by viewModel.uiState.collectAsState()
      val discoveredPeers by viewModel.discoveredPeers.collectAsState()
      val categories by viewModel.categories.collectAsState()
      val activeTransfer by viewModel.activeTransfer.collectAsState()
      val aiReport by viewModel.aiReport.collectAsState()
      val verificationReport by viewModel.verificationReport.collectAsState()
      val allTransfers by viewModel.allTransfers.collectAsState()
      val recentTransfers by viewModel.recentTransfers.collectAsState()
      val completedCount by viewModel.completedCount.collectAsState()
      val totalBytes by viewModel.totalBytesTransferred.collectAsState()

      FlowTransferTheme(darkTheme = uiState.isDarkTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
          FlowTransferApp(
            uiState = uiState,
            discoveredPeers = discoveredPeers,
            categories = categories,
            activeTransfer = activeTransfer,
            aiReport = aiReport,
            verificationReport = verificationReport,
            allTransfers = allTransfers,
            recentTransfers = recentTransfers,
            completedCount = completedCount,
            totalBytes = totalBytes ?: 0L,
            viewModel = viewModel
          )
        }
      }
    }
  }
}

@Composable
fun FlowTransferApp(
  uiState: com.example.viewmodel.FlowUiState,
  discoveredPeers: List<com.example.model.DevicePeer>,
  categories: List<com.example.model.CategorySummary>,
  activeTransfer: com.example.model.ActiveTransferState,
  aiReport: com.example.model.AiOptimizationReport?,
  verificationReport: com.example.model.VerificationReport?,
  allTransfers: List<com.example.data.TransferRecordEntity>,
  recentTransfers: List<com.example.data.TransferRecordEntity>,
  completedCount: Int,
  totalBytes: Long,
  viewModel: FlowTransferViewModel
) {
  when (uiState.currentScreen) {
    ScreenState.SPLASH -> {
      SplashScreen(
        onGetStarted = { viewModel.navigateTo(ScreenState.HOME) }
      )
    }

    ScreenState.HOME -> {
      HomeScreen(
        onSendClick = { viewModel.startTransferFlow(com.example.model.TransferMode.SENDER) },
        onReceiveClick = { viewModel.startTransferFlow(com.example.model.TransferMode.RECEIVER) },
        onPcMacClick = { viewModel.navigateTo(ScreenState.PC_MAC_PORTAL) },
        onBackupClick = { viewModel.navigateTo(ScreenState.WIRELESS_BACKUP) },
        onHistoryClick = { viewModel.navigateTo(ScreenState.TRANSFER_HISTORY) },
        onSettingsClick = { viewModel.navigateTo(ScreenState.SETTINGS) },
        recentTransfers = recentTransfers,
        completedCount = completedCount,
        totalBytes = totalBytes,
        selectedTab = uiState.selectedTab,
        onTabSelected = { viewModel.setTab(it) }
      )
    }

    ScreenState.DISCOVERY -> {
      BackHandler {
        viewModel.navigateTo(ScreenState.HOME)
      }
      DeviceDiscoveryScreen(
        mode = uiState.activeMode,
        peers = discoveredPeers,
        selectedPeer = uiState.selectedPeer,
        isPairingDialogVisible = uiState.isPairingDialogVisible,
        onPeerSelected = { viewModel.selectPeerForPairing(it) },
        onConfirmPairing = { viewModel.confirmPairing() },
        onDismissPairing = { viewModel.dismissPairingDialog() },
        onBackClick = { viewModel.navigateTo(ScreenState.HOME) },
        onQrCodeScanned = { viewModel.onQrCodeScanned(it) }
      )
    }

    ScreenState.DATA_SELECTION -> {
      BackHandler {
        viewModel.navigateTo(ScreenState.DISCOVERY)
      }
      DataSelectionScreen(
        categories = categories,
        peer = uiState.selectedPeer,
        aiReport = aiReport,
        isAiModalVisible = uiState.isAiModalVisible,
        isOsLimitationSheetVisible = uiState.isOsLimitationSheetVisible,
        onToggleCategory = { viewModel.toggleCategory(it) },
        onSelectAll = { viewModel.selectAllData(it) },
        onSmartRecommendations = { viewModel.applySmartRecommendations() },
        onRunAiOptimization = { viewModel.runAiOptimization() },
        onApplyAiCleanup = { viewModel.applyAiCleanup() },
        onCloseAiModal = { viewModel.closeAiModal() },
        onToggleOsLimitationSheet = { viewModel.toggleOsLimitationSheet(it) },
        onStartTransfer = { viewModel.startTransfer() },
        onBackClick = { viewModel.navigateTo(ScreenState.DISCOVERY) }
      )
    }

    ScreenState.ACTIVE_TRANSFER -> {
      BackHandler {
        viewModel.cancelTransfer()
      }
      ActiveTransferScreen(
        transferState = activeTransfer,
        onPauseClick = { viewModel.pauseTransfer() },
        onResumeClick = { viewModel.resumeTransfer() },
        onCancelClick = { viewModel.cancelTransfer() },
        onSimulateDropClick = { viewModel.simulateDropAndResume() },
        onFinishClick = { viewModel.finishTransfer() }
      )
    }

    ScreenState.COMPLETION_REPORT -> {
      BackHandler {
        viewModel.navigateTo(ScreenState.HOME)
      }
      CompletionScreen(
        report = verificationReport,
        onDoneClick = { viewModel.navigateTo(ScreenState.HOME) }
      )
    }

    ScreenState.PC_MAC_PORTAL -> {
      BackHandler {
        viewModel.navigateTo(ScreenState.HOME)
      }
      PcMacPortalScreen(
        onBackClick = { viewModel.navigateTo(ScreenState.HOME) }
      )
    }

    ScreenState.WIRELESS_BACKUP -> {
      BackHandler {
        viewModel.navigateTo(ScreenState.HOME)
      }
      WirelessBackupScreen(
        isBackupInProgress = uiState.backupInProgress,
        backupProgress = uiState.backupProgress,
        isBackupCompleted = uiState.backupCompleted,
        onStartBackup = { viewModel.startWirelessBackup() },
        onBackClick = { viewModel.navigateTo(ScreenState.HOME) }
      )
    }

    ScreenState.TRANSFER_HISTORY -> {
      BackHandler {
        viewModel.navigateTo(ScreenState.HOME)
      }
      TransferHistoryScreen(
        transfers = allTransfers,
        onDeleteRecord = { viewModel.deleteHistoryRecord(it) },
        onClearAll = { viewModel.clearHistory() },
        onBackClick = { viewModel.navigateTo(ScreenState.HOME) }
      )
    }

    ScreenState.SETTINGS -> {
      BackHandler {
        viewModel.navigateTo(ScreenState.HOME)
      }
      SettingsScreen(
        isDarkTheme = uiState.isDarkTheme,
        onThemeChanged = { viewModel.setDarkTheme(it) },
        isE2eeEnabled = uiState.isE2eeEnabled,
        onE2eeChanged = { viewModel.setE2eeEnabled(it) },
        selectedLanguage = uiState.appLanguage,
        onLanguageChanged = { viewModel.setLanguage(it) },
        preferredConnection = uiState.preferredConnection,
        onConnectionChanged = { viewModel.setPreferredConnection(it) },
        onBackClick = { viewModel.navigateTo(ScreenState.HOME) }
      )
    }
  }
}
