package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TransferRepository
import com.example.model.ActiveTransferState
import com.example.model.DeviceType
import com.example.model.TransferMode
import com.example.model.TransferStatus
import com.example.ui.components.AnimatedTransferProgressGauge
import com.example.ui.components.FlowTopBar
import com.example.ui.theme.FlowAccentCyan
import com.example.ui.theme.FlowAccentEmerald
import com.example.ui.theme.FlowDeepNavy
import com.example.ui.theme.FlowNavy700
import com.example.ui.theme.FlowNavy800
import com.example.ui.theme.FlowStatusWarning

@Composable
fun ActiveTransferScreen(
  transferState: ActiveTransferState,
  onPauseClick: () -> Unit,
  onResumeClick: () -> Unit,
  onCancelClick: () -> Unit,
  onSimulateDropClick: () -> Unit,
  onFinishClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  // If status is COMPLETED or VERIFYING, navigate to completion screen
  LaunchedEffect(transferState.status) {
    if (transferState.status == TransferStatus.COMPLETED) {
      onFinishClick()
    }
  }

  val infiniteTransition = rememberInfiniteTransition(label = "transferAnimation")
  val wavePhase by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "wavePhase"
  )

  Scaffold(
    topBar = {
      FlowTopBar(
        title = if (transferState.transferMode == TransferMode.SENDER) "Sending to Peer" else "Receiving from Peer",
        subtitle = "Session: #${transferState.sessionId}",
        showBack = false,
        actions = {
          IconButton(onClick = onCancelClick, modifier = Modifier.testTag("cancel_transfer_icon_button")) {
            Icon(Icons.Default.Close, contentDescription = "Cancel Transfer")
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
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Device Flow Peer Connection Bar
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Source Device
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
              shape = CircleShape,
              color = FlowAccentCyan.copy(alpha = 0.2f),
              modifier = Modifier.size(52.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.PhoneAndroid,
                  contentDescription = "My Device",
                  tint = FlowAccentCyan,
                  modifier = Modifier.size(28.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Pixel 9 Pro", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          }

          // Animated Flow Stream between devices
          Box(
            modifier = Modifier
              .weight(1f)
              .height(30.dp)
              .padding(horizontal = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
              val start = Offset(0f, size.height / 2f)
              val end = Offset(size.width, size.height / 2f)
              drawLine(
                color = FlowAccentCyan.copy(alpha = 0.3f),
                start = start,
                end = end,
                strokeWidth = 3.dp.toPx()
              )

              // Moving packet pulses
              for (p in 0..2) {
                val offsetRatio = (wavePhase + (p / 3f)) % 1f
                val packetX = size.width * offsetRatio
                drawCircle(
                  color = FlowAccentEmerald,
                  radius = 5.dp.toPx(),
                  center = Offset(packetX, size.height / 2f)
                )
              }
            }
          }

          // Target Device
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
              shape = CircleShape,
              color = FlowAccentEmerald.copy(alpha = 0.2f),
              modifier = Modifier.size(52.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = if (transferState.peerDevice?.deviceType == DeviceType.IPHONE)
                    Icons.Default.PhoneIphone
                  else Icons.Default.PhoneAndroid,
                  contentDescription = "Target Peer",
                  tint = FlowAccentEmerald,
                  modifier = Modifier.size(28.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = transferState.peerDevice?.name?.take(14) ?: "Target Device",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              maxLines = 1
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Connection & Encryption Tag
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.padding(horizontal = 8.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = FlowAccentEmerald, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "${transferState.connectionType.displayName} • ${transferState.encryptionCipher} Protected",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // Center Animated Progress Gauge with Real-Time Speed & ETA Telemetry
      AnimatedTransferProgressGauge(
        progressPercent = transferState.progressPercent,
        speedMbps = transferState.currentSpeedMbps,
        remainingTimeSeconds = transferState.remainingTimeSeconds,
        transferredFiles = transferState.transferredFiles,
        totalFiles = transferState.totalFiles,
        status = transferState.status,
        gaugeSize = 256.dp
      )

      // Current File Transmitting Panel
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {

        // Current File Transmitting Card
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = FlowAccentCyan.copy(alpha = 0.2f),
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = transferState.currentCategory.icon,
                  contentDescription = null,
                  tint = FlowAccentCyan,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = if (transferState.isReconnecting) "Reconnecting..." else transferState.currentFileName,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = "${TransferRepository.formatBytes(transferState.transferredBytes)} of ${TransferRepository.formatBytes(transferState.totalBytes)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Reconnect Alert if triggered
        if (transferState.status == TransferStatus.RECONNECTING) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = FlowStatusWarning.copy(alpha = 0.2f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.WifiOff, contentDescription = null, tint = FlowStatusWarning, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Network blip detected. Auto-reconnecting and resuming from current file...",
                style = MaterialTheme.typography.labelSmall,
                color = FlowStatusWarning
              )
            }
          }
        }
      }

      // Bottom Control Buttons
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          if (transferState.status == TransferStatus.PAUSED) {
            Button(
              onClick = onResumeClick,
              colors = ButtonDefaults.buttonColors(
                containerColor = FlowAccentCyan,
                contentColor = FlowDeepNavy
              ),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("resume_transfer_button")
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Resume", fontWeight = FontWeight.Bold)
            }
          } else {
            OutlinedButton(
              onClick = onPauseClick,
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("pause_transfer_button")
            ) {
              Icon(Icons.Default.Pause, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Pause", fontWeight = FontWeight.SemiBold)
            }
          }

          // Resilience Reconnection Test Button
          OutlinedButton(
            onClick = onSimulateDropClick,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("test_auto_reconnect_button")
          ) {
            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Test Drop & Resume", fontSize = 11.sp)
          }
        }
      }
    }
  }
}
