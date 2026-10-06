package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DevicePeer
import com.example.model.TransferMode
import com.example.ui.components.CameraQrScannerView
import com.example.ui.components.DevicePeerCard
import com.example.ui.components.FlowTopBar
import com.example.ui.components.PairingDialog
import com.example.ui.components.QrCodeVisualizer
import com.example.ui.components.RadarScanVisualizer
import com.example.ui.theme.FlowAccentCyan
import com.example.ui.theme.FlowAccentEmerald
import com.example.ui.theme.FlowDeepNavy

@Composable
fun DeviceDiscoveryScreen(
  mode: TransferMode,
  peers: List<DevicePeer>,
  selectedPeer: DevicePeer?,
  isPairingDialogVisible: Boolean,
  onPeerSelected: (DevicePeer) -> Unit,
  onConfirmPairing: () -> Unit,
  onDismissPairing: () -> Unit,
  onBackClick: () -> Unit,
  onQrCodeScanned: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Radar Discovery, 1: QR Code Pairing
  var isCameraScannerOpen by remember { mutableStateOf(false) }

  if (isCameraScannerOpen) {
    CameraQrScannerView(
      onQrCodeScanned = { payload ->
        isCameraScannerOpen = false
        onQrCodeScanned(payload)
      },
      onDismiss = { isCameraScannerOpen = false }
    )
    return
  }

  Scaffold(
    topBar = {
      FlowTopBar(
        title = if (mode == TransferMode.SENDER) "Find Target Device" else "Waiting for Sender",
        subtitle = "Scanning Wi-Fi Direct, Local LAN & BLE",
        showBack = true,
        onBackClick = onBackClick,
        actions = {
          IconButton(
            onClick = { isCameraScannerOpen = true },
            modifier = Modifier.testTag("open_camera_scanner_icon_button")
          ) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Scan QR with CameraX", tint = FlowAccentCyan)
          }
        }
      )
    },
    containerColor = MaterialTheme.colorScheme.background,
    modifier = modifier
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // Toggle Tabs between Radar & QR
      TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = FlowAccentCyan,
        indicator = { tabPositions ->
          TabRowDefaults.SecondaryIndicator(
            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
            color = FlowAccentCyan
          )
        }
      ) {
        Tab(
          selected = selectedTabIndex == 0,
          onClick = { selectedTabIndex = 0 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Radar, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Radar Scanner", fontWeight = FontWeight.SemiBold)
            }
          },
          modifier = Modifier.testTag("tab_radar_scanner")
        )
        Tab(
          selected = selectedTabIndex == 1,
          onClick = { selectedTabIndex = 1 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("QR Code Pair", fontWeight = FontWeight.SemiBold)
            }
          },
          modifier = Modifier.testTag("tab_qr_code")
        )
      }

      if (selectedTabIndex == 0) {
        // Radar Discovery View
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          item {
            Spacer(modifier = Modifier.height(10.dp))
            RadarScanVisualizer(modifier = Modifier.padding(vertical = 12.dp))
            Text(
              text = "Searching for nearby devices...",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Ensure both devices are on the same Wi-Fi or have Wi-Fi turned on.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
          }

          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Nearby Devices (${peers.size})",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
              )
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = FlowAccentCyan.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "Broadcasting",
                  style = MaterialTheme.typography.labelSmall,
                  color = FlowAccentCyan,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          items(peers) { peer ->
            DevicePeerCard(
              peer = peer,
              onClick = { onPeerSelected(peer) }
            )
          }

          item {
            Spacer(modifier = Modifier.height(24.dp))
          }
        }
      } else {
        // QR Code Pairing View
        LazyColumn(
          modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
              text = "Direct Camera Pairing",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Scan this code with the other device's FlowTransfer scanner to pair automatically without searching.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center,
              modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
          }

          item {
            QrCodeVisualizer(
              payloadText = "flowtransfer://p2p?session=849201&ip=192.168.49.1&device=FlowDevice&auth=aes256"
            )
          }

          item {
            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "Pairing Security Token",
                  style = MaterialTheme.typography.labelMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "619 420",
                  style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Bold
                  ),
                  color = FlowAccentCyan
                )
              }
            }
          }

          item {
            Button(
              onClick = { isCameraScannerOpen = true },
              colors = ButtonDefaults.buttonColors(
                containerColor = FlowAccentCyan,
                contentColor = FlowDeepNavy
              ),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("scan_other_qr_button")
            ) {
              Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Launch CameraX QR Scanner", fontWeight = FontWeight.Bold)
            }
          }

          item {
            Spacer(modifier = Modifier.height(20.dp))
          }
        }
      }
    }
  }

  // Pairing Handshake Confirmation Dialog
  if (isPairingDialogVisible && selectedPeer != null) {
    PairingDialog(
      peer = selectedPeer,
      onConfirm = onConfirmPairing,
      onDismiss = onDismissPairing
    )
  }
}
