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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.MetallicLuxuryLogo
import com.example.ui.theme.FlowAccentCyan
import com.example.ui.theme.FlowAccentEmerald
import com.example.ui.theme.FlowDarkBackground
import com.example.ui.theme.FlowDeepNavy

@Composable
fun SplashScreen(
  onGetStarted: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "splash")

  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.92f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse"
  )

  val rotationAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(8000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "rotation"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            FlowDarkBackground,
            FlowDeepNavy,
            Color(0xFF0F1E42)
          )
        )
      )
      .clickable { onGetStarted() }
      .testTag("splash_screen_root"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.padding(24.dp)
    ) {
      // Luxury Metallic Emblem with Specular Gleam
      Box(
        modifier = Modifier.size(160.dp),
        contentAlignment = Alignment.Center
      ) {
        // Subtle ambient rotating metallic halo
        Canvas(modifier = Modifier.fillMaxSize().rotate(rotationAngle)) {
          val strokeWidth = 2.dp.toPx()
          val radius = (size.minDimension - strokeWidth) / 2f
          drawArc(
            brush = Brush.sweepGradient(
              listOf(
                Color(0xFFE2E8F0),
                Color(0xFFF59E0B),
                Color.Transparent,
                Color(0xFF94A3B8),
                Color(0xFFE2E8F0)
              )
            ),
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
            size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
            style = Stroke(width = strokeWidth)
          )
        }

        // Precision Luxury Metallic Logo
        MetallicLuxuryLogo(
          size = 136.dp,
          withGleamAnimation = true,
          modifier = Modifier.scale(pulseScale)
        )
      }

      Spacer(modifier = Modifier.height(32.dp))

      Text(
        text = "FlowTransfer",
        style = MaterialTheme.typography.displayLarge.copy(
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = (-0.5).sp
        ),
        color = Color.White
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Move Your Digital Life.",
        style = MaterialTheme.typography.titleMedium,
        color = FlowAccentCyan,
        fontWeight = FontWeight.Medium
      )

      Spacer(modifier = Modifier.height(48.dp))

      Text(
        text = "Tap anywhere to continue",
        style = MaterialTheme.typography.bodySmall,
        color = Color.White.copy(alpha = 0.5f)
      )
    }
  }
}
