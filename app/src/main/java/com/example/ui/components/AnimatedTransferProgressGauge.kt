package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TransferStatus
import com.example.ui.theme.FlowAccentCyan
import com.example.ui.theme.FlowAccentEmerald
import com.example.ui.theme.FlowDeepNavy
import com.example.ui.theme.FlowNavy700
import com.example.ui.theme.FlowNavy800
import com.example.ui.theme.FlowStatusWarning
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedTransferProgressGauge(
  progressPercent: Float,
  speedMbps: Double,
  remainingTimeSeconds: Long,
  transferredFiles: Int,
  totalFiles: Int,
  status: TransferStatus,
  modifier: Modifier = Modifier,
  gaugeSize: Dp = 260.dp
) {
  // 1. Smoothly animated progress arc
  val animatedProgress by animateFloatAsState(
    targetValue = progressPercent.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
    label = "animatedProgress"
  )

  // 2. Smoothly animated speed reading
  val animatedSpeed by animateFloatAsState(
    targetValue = speedMbps.toFloat(),
    animationSpec = spring(dampingRatio = 0.85f, stiffness = 300f),
    label = "animatedSpeed"
  )

  // 3. Ambient Infinite Animation Transitions
  val infiniteTransition = rememberInfiniteTransition(label = "gaugeAnimations")

  // Shimmer rotation angle running along the arc
  val shimmerRotation by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 3000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "shimmerRotation"
  )

  // Glowing comet head pulse at leading tip
  val headPulseRadius by infiniteTransition.animateFloat(
    initialValue = 6f,
    targetValue = 11f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "headPulseRadius"
  )

  // Animated live transmission waveform bars
  val wavePhase1 by infiniteTransition.animateFloat(
    initialValue = 0.2f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "wavePhase1"
  )
  val wavePhase2 by infiniteTransition.animateFloat(
    initialValue = 0.3f,
    targetValue = 0.9f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 550, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "wavePhase2"
  )
  val wavePhase3 by infiniteTransition.animateFloat(
    initialValue = 0.15f,
    targetValue = 0.85f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 480, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "wavePhase3"
  )

  // Dynamic Theme Colors based on Status
  val (primaryArcColor, secondaryArcColor, haloGlow) = when (status) {
    TransferStatus.PAUSED -> Triple(Color(0xFF94A3B8), Color(0xFF64748B), Color(0x3394A3B8))
    TransferStatus.RECONNECTING -> Triple(FlowStatusWarning, Color(0xFFD97706), Color(0x44F59E0B))
    TransferStatus.VERIFYING, TransferStatus.COMPLETED -> Triple(FlowAccentEmerald, Color(0xFF34D399), Color(0x4410B981))
    else -> Triple(FlowAccentCyan, FlowAccentEmerald, Color(0x3306B6D4))
  }

  Box(
    modifier = modifier
      .size(gaugeSize)
      .testTag("animated_transfer_progress_gauge"),
    contentAlignment = Alignment.Center
  ) {
    // Canvas: Multi-layer Glowing Arc, Outer Ticks & Tip Particle
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height
      val center = Offset(w / 2f, h / 2f)

      val arcStrokeWidth = 14.dp.toPx()
      val arcRadius = (w.coerceAtMost(h) - arcStrokeWidth * 2.5f) / 2f

      // A. Outer Concentric Precision Tick Ring (60 radial ticks)
      val tickCount = 60
      val tickLength = 5.dp.toPx()
      val outerTickRadius = arcRadius + arcStrokeWidth * 0.9f

      for (i in 0 until tickCount) {
        val angleDeg = (i * 360f / tickCount) - 90f
        val angleRad = Math.toRadians(angleDeg.toDouble())

        val startX = (center.x + (outerTickRadius) * cos(angleRad)).toFloat()
        val startY = (center.y + (outerTickRadius) * sin(angleRad)).toFloat()
        val endX = (center.x + (outerTickRadius + tickLength) * cos(angleRad)).toFloat()
        val endY = (center.y + (outerTickRadius + tickLength) * sin(angleRad)).toFloat()

        val progressTickAngle = animatedProgress * 360f
        val isTickPassed = (i * 360f / tickCount) <= progressTickAngle

        val tickColor = if (isTickPassed && status != TransferStatus.PAUSED) {
          primaryArcColor.copy(alpha = 0.75f)
        } else {
          FlowNavy700.copy(alpha = 0.25f)
        }

        drawLine(
          color = tickColor,
          start = Offset(startX, startY),
          end = Offset(endX, endY),
          strokeWidth = 1.5.dp.toPx(),
          cap = StrokeCap.Round
        )
      }

      // B. Recessed Dark Track Background Ring
      drawCircle(
        color = FlowNavy800.copy(alpha = 0.45f),
        radius = arcRadius,
        center = center,
        style = Stroke(width = arcStrokeWidth)
      )

      // C. Active Animated Progress Arc
      val sweepAngle = 360f * animatedProgress
      if (sweepAngle > 0f) {
        // Outer Ambient Bloom Shadow Arc
        drawArc(
          color = haloGlow,
          startAngle = -90f,
          sweepAngle = sweepAngle,
          useCenter = false,
          topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
          size = Size(arcRadius * 2f, arcRadius * 2f),
          style = Stroke(width = arcStrokeWidth * 1.5f, cap = StrokeCap.Round)
        )

        // Main Sweep Gradient Progress Arc with dynamic shimmer
        val arcBrush = Brush.sweepGradient(
          listOf(
            primaryArcColor,
            secondaryArcColor,
            Color.White.copy(alpha = 0.85f),
            primaryArcColor
          ),
          center = center
        )

        drawArc(
          brush = arcBrush,
          startAngle = -90f,
          sweepAngle = sweepAngle,
          useCenter = false,
          topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
          size = Size(arcRadius * 2f, arcRadius * 2f),
          style = Stroke(width = arcStrokeWidth, cap = StrokeCap.Round)
        )

        // D. Pulsing Leading-Edge Particle (Comet Head) at Tip of Arc
        val tipAngleDeg = -90f + sweepAngle
        val tipAngleRad = Math.toRadians(tipAngleDeg.toDouble())
        val tipX = (center.x + arcRadius * cos(tipAngleRad)).toFloat()
        val tipY = (center.y + arcRadius * sin(tipAngleRad)).toFloat()
        val tipOffset = Offset(tipX, tipY)

        if (status != TransferStatus.PAUSED) {
          // Soft outer pulse glow
          drawCircle(
            color = primaryArcColor.copy(alpha = 0.45f),
            radius = headPulseRadius.dp.toPx(),
            center = tipOffset
          )
          // Solid white core particle
          drawCircle(
            color = Color.White,
            radius = 4.dp.toPx(),
            center = tipOffset
          )
        }
      }
    }

    // Center Telemetry Overlay: % + Live Speed + Files + ETA
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(28.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // 1. Status Tag / Reconnect Indicator
      if (status == TransferStatus.PAUSED) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF64748B).copy(alpha = 0.25f),
          modifier = Modifier.padding(bottom = 4.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Pause, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("PAUSED", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = Color(0xFFE2E8F0), fontSize = 10.sp)
          }
        }
      } else if (status == TransferStatus.RECONNECTING) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = FlowStatusWarning.copy(alpha = 0.25f),
          modifier = Modifier.padding(bottom = 4.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Sync, contentDescription = null, tint = FlowStatusWarning, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("RECONNECTING", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = FlowStatusWarning, fontSize = 10.sp)
          }
        }
      } else if (status == TransferStatus.VERIFYING) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = FlowAccentEmerald.copy(alpha = 0.25f),
          modifier = Modifier.padding(bottom = 4.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Verified, contentDescription = null, tint = FlowAccentEmerald, modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("VERIFYING", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = FlowAccentEmerald, fontSize = 10.sp)
          }
        }
      }

      // 2. Large Bold Percentage
      Text(
        text = "${(animatedProgress * 100).toInt()}%",
        style = MaterialTheme.typography.displayLarge.copy(
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = (-1.5).sp,
          fontSize = 44.sp
        ),
        color = MaterialTheme.colorScheme.onBackground
      )

      // 3. Live Animated Equalizer Speed Waveform
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 2.dp)
      ) {
        if (status == TransferStatus.TRANSFERRING) {
          // Dynamic Wave Bars reflecting active packet stream
          Row(
            horizontalArrangement = Arrangement.spacedBy(2.5.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 6.dp)
          ) {
            WaveBar(heightScale = wavePhase1, color = FlowAccentCyan)
            WaveBar(heightScale = wavePhase2, color = FlowAccentEmerald)
            WaveBar(heightScale = wavePhase3, color = FlowAccentCyan)
          }
        }

        Text(
          text = if (status == TransferStatus.PAUSED) "0.0 MB/s" else "${String.format(java.util.Locale.US, "%.1f", animatedSpeed)} MB/s",
          style = MaterialTheme.typography.labelLarge.copy(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold
          ),
          color = if (status == TransferStatus.PAUSED) Color(0xFF94A3B8) else FlowAccentCyan
        )
      }

      // 4. File Count Progress
      Text(
        text = "$transferredFiles / $totalFiles files",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(8.dp))

      // 5. ETA Countdown Pill Badge
      val formattedEta = formatRemainingTime(remainingTimeSeconds, status)
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.HourglassBottom,
            contentDescription = "ETA",
            tint = if (status == TransferStatus.PAUSED) Color(0xFF94A3B8) else FlowAccentEmerald,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = formattedEta,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (status == TransferStatus.PAUSED) Color(0xFF94A3B8) else FlowAccentEmerald,
            fontSize = 11.sp
          )
        }
      }
    }
  }
}

@Composable
private fun WaveBar(
  heightScale: Float,
  color: Color
) {
  val barHeight = (4.dp + (10.dp * heightScale.coerceIn(0f, 1f)))
  Box(
    modifier = Modifier
      .width(2.5.dp)
      .height(barHeight)
      .background(color, RoundedCornerShape(2.dp))
  )
}

private fun formatRemainingTime(seconds: Long, status: TransferStatus): String {
  if (status == TransferStatus.PAUSED) return "Paused"
  if (status == TransferStatus.VERIFYING) return "Finalizing..."
  if (status == TransferStatus.COMPLETED) return "Completed"
  if (seconds <= 0) return "< 5s remaining"

  val mins = seconds / 60
  val secs = seconds % 60
  return when {
    mins > 0 -> "${mins}m ${secs}s remaining"
    else -> "${secs}s remaining"
  }
}
