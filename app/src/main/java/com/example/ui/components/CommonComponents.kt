package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TabletMac
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CategorySummary
import com.example.model.ConnectionType
import com.example.model.DevicePeer
import com.example.model.DeviceType
import com.example.ui.theme.FlowAccentCyan
import com.example.ui.theme.FlowAccentEmerald
import com.example.ui.theme.FlowDarkBackground
import com.example.ui.theme.FlowDeepNavy
import com.example.ui.theme.FlowNavy700
import com.example.ui.theme.FlowNavy800
import com.example.ui.theme.FlowStatusSuccess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlowTopBar(
  title: String,
  subtitle: String? = null,
  showBack: Boolean = false,
  onBackClick: () -> Unit = {},
  showEncryptionBadge: Boolean = true,
  actions: @Composable () -> Unit = {}
) {
  TopAppBar(
    title = {
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        if (subtitle != null) {
          Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    },
    navigationIcon = {
      if (showBack) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier.testTag("top_bar_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back"
          )
        }
      } else {
        Box(modifier = Modifier.padding(start = 16.dp, end = 4.dp)) {
          MetallicLuxuryLogo(size = 32.dp, withGleamAnimation = false)
        }
      }
    },
    actions = {
      if (showEncryptionBadge) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = FlowAccentEmerald.copy(alpha = 0.15f),
          modifier = Modifier.padding(end = 8.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "E2EE Secured",
              tint = FlowAccentEmerald,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "AES-256",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = FlowAccentEmerald,
              fontSize = 10.sp
            )
          }
        }
      }
      actions()
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.background,
      titleContentColor = MaterialTheme.colorScheme.onBackground
    )
  )
}

@Composable
fun RadarScanVisualizer(
  modifier: Modifier = Modifier,
  pulseCount: Int = 3
) {
  val infiniteTransition = rememberInfiniteTransition(label = "radar")
  val progress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2400, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "radarPulse"
  )

  Box(
    modifier = modifier.size(240.dp),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val centerOffset = Offset(size.width / 2f, size.height / 2f)
      val maxRadius = size.width / 2f

      for (i in 0 until pulseCount) {
        val currentProgress = (progress + (i.toFloat() / pulseCount.toFloat())) % 1f
        val radius = maxRadius * currentProgress
        val alpha = (1f - currentProgress).coerceIn(0f, 0.7f)

        drawCircle(
          color = FlowAccentCyan.copy(alpha = alpha * 0.4f),
          radius = radius,
          center = centerOffset,
          style = Stroke(width = 2.5f)
        )
      }

      // Center glowing core
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(FlowAccentCyan, FlowAccentEmerald.copy(alpha = 0.2f), Color.Transparent),
          center = centerOffset,
          radius = 45.dp.toPx()
        ),
        radius = 45.dp.toPx(),
        center = centerOffset
      )
    }

    // Center icon
    Surface(
      shape = CircleShape,
      color = FlowNavy800,
      border = androidx.compose.foundation.BorderStroke(2.dp, FlowAccentCyan),
      modifier = Modifier.size(56.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = Icons.Default.Wifi,
          contentDescription = "Radar Scanner",
          tint = FlowAccentCyan,
          modifier = Modifier.size(26.dp)
        )
      }
    }
  }
}

@Composable
fun QrCodeVisualizer(
  payloadText: String,
  modifier: Modifier = Modifier
) {
  // Renders a high-fidelity stylized QR Matrix for device pairing
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(200.dp)
          .background(Color.White),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val matrixSize = 25
          val cellSize = size.width / matrixSize

          // Background white
          drawRect(Color.White)

          // Helper to check finder patterns (top-left, top-right, bottom-left 7x7)
          fun isFinder(r: Int, c: Int): Boolean {
            val inTL = r in 0..6 && c in 0..6
            val inTR = r in 0..6 && c in (matrixSize - 7) until matrixSize
            val inBL = r in (matrixSize - 7) until matrixSize && c in 0..6
            return inTL || inTR || inBL
          }

          // Deterministic pseudo-random pattern based on payload hash
          val seed = payloadText.hashCode()
          for (r in 0 until matrixSize) {
            for (c in 0 until matrixSize) {
              if (isFinder(r, c)) continue
              val hash = (seed * 31 + r * 17 + c * 43).and(0x7fffffff)
              if (hash % 3 != 0) {
                drawRect(
                  color = FlowDeepNavy,
                  topLeft = Offset(c * cellSize, r * cellSize),
                  size = Size(cellSize - 0.5f, cellSize - 0.5f)
                )
              }
            }
          }

          // Draw official 3 Finder Patterns
          fun drawFinderSquare(startCol: Int, startRow: Int) {
            // Outer 7x7
            drawRect(
              color = FlowDeepNavy,
              topLeft = Offset(startCol * cellSize, startRow * cellSize),
              size = Size(7 * cellSize, 7 * cellSize)
            )
            // Inner 5x5 white
            drawRect(
              color = Color.White,
              topLeft = Offset((startCol + 1) * cellSize, (startRow + 1) * cellSize),
              size = Size(5 * cellSize, 5 * cellSize)
            )
            // Center 3x3 solid
            drawRect(
              color = FlowAccentCyan,
              topLeft = Offset((startCol + 2) * cellSize, (startRow + 2) * cellSize),
              size = Size(3 * cellSize, 3 * cellSize)
            )
          }

          drawFinderSquare(0, 0)
          drawFinderSquare(matrixSize - 7, 0)
          drawFinderSquare(0, matrixSize - 7)

          // Center logo badge inside QR
          drawCircle(
            color = FlowDeepNavy,
            radius = cellSize * 2f,
            center = Offset(size.width / 2f, size.height / 2f)
          )
          drawCircle(
            color = FlowAccentEmerald,
            radius = cellSize * 1.2f,
            center = Offset(size.width / 2f, size.height / 2f)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = "Scan to Pair Instantly",
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
        color = FlowDeepNavy
      )
    }
  }
}

@Composable
fun DevicePeerCard(
  peer: DevicePeer,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val icon: ImageVector = when (peer.deviceType) {
    DeviceType.ANDROID -> Icons.Default.PhoneAndroid
    DeviceType.IPHONE -> Icons.Default.PhoneIphone
    DeviceType.TABLET_ANDROID, DeviceType.IPAD -> Icons.Default.TabletMac
    DeviceType.MAC, DeviceType.WINDOWS_PC -> Icons.Default.Computer
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .testTag("device_card_${peer.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant
    ),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = CircleShape,
        color = when (peer.deviceType) {
          DeviceType.IPHONE, DeviceType.IPAD, DeviceType.MAC -> FlowAccentCyan.copy(alpha = 0.18f)
          else -> FlowAccentEmerald.copy(alpha = 0.18f)
        },
        modifier = Modifier.size(48.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = icon,
            contentDescription = peer.name,
            tint = when (peer.deviceType) {
              DeviceType.IPHONE, DeviceType.IPAD, DeviceType.MAC -> FlowAccentCyan
              else -> FlowAccentEmerald
            },
            modifier = Modifier.size(26.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = peer.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = peer.deviceType.displayName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = " • ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = peer.connectionType.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = FlowAccentCyan,
            fontWeight = FontWeight.Medium
          )
        }
      }

      Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
          containerColor = FlowAccentCyan,
          contentColor = FlowDeepNavy
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("pair_button_${peer.id}")
      ) {
        Text("Connect", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
      }
    }
  }
}

@Composable
fun ContentCategorySelectionCard(
  categorySummary: CategorySummary,
  onToggle: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable { onToggle() }
      .testTag("category_card_${categorySummary.category.name}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (categorySummary.isSelected)
        MaterialTheme.colorScheme.surfaceVariant
      else
        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
    ),
    border = androidx.compose.foundation.BorderStroke(
      1.dp,
      if (categorySummary.isSelected) FlowAccentCyan.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Checkbox(
        checked = categorySummary.isSelected,
        onCheckedChange = { onToggle() },
        colors = CheckboxDefaults.colors(
          checkedColor = FlowAccentCyan,
          checkmarkColor = FlowDeepNavy
        ),
        modifier = Modifier.testTag("checkbox_${categorySummary.category.name}")
      )

      Surface(
        shape = RoundedCornerShape(10.dp),
        color = FlowNavy700.copy(alpha = 0.4f),
        modifier = Modifier.size(40.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = categorySummary.category.icon,
            contentDescription = categorySummary.category.displayName,
            tint = if (categorySummary.isSelected) FlowAccentCyan else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = categorySummary.category.displayName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
          )
          if (!categorySummary.category.iosSupported) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = Color(0xFFF59E0B).copy(alpha = 0.2f)
            ) {
              Text(
                text = "iOS Sandbox",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFF59E0B),
                fontSize = 9.sp,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
              )
            }
          }
        }
        Text(
          text = "${categorySummary.itemCount} items • ${categorySummary.formattedSize}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
fun PairingDialog(
  peer: DevicePeer,
  onConfirm: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Security,
          contentDescription = "Secure Pairing",
          tint = FlowAccentEmerald,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Secure Device Handshake", style = MaterialTheme.typography.titleMedium)
      }
    },
    text = {
      Column {
        Text(
          text = "Confirm 6-digit cryptographic PIN with ${peer.name}:",
          style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(14.dp))
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = peer.sessionPin,
            style = MaterialTheme.typography.displayMedium.copy(
              fontFamily = FontFamily.Monospace,
              letterSpacing = 6.sp,
              fontWeight = FontWeight.Bold
            ),
            color = FlowAccentCyan,
            textAlign = TextAlign.Center,
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp)
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = FlowAccentEmerald,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Direct peer-to-peer 256-bit AES encryption active.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onConfirm,
        colors = ButtonDefaults.buttonColors(
          containerColor = FlowAccentEmerald,
          contentColor = FlowDeepNavy
        ),
        modifier = Modifier.testTag("confirm_pairing_button")
      ) {
        Text("Verify & Connect", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        modifier = Modifier.testTag("cancel_pairing_button")
      ) {
        Text("Cancel")
      }
    }
  )
}
