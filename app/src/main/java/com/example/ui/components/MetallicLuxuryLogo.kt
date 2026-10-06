package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Luxury Metallic Palette Tokens
val PlatinumHighlight = Color(0xFFFFFFFF)
val PlatinumLight = Color(0xFFE2E8F0)
val PlatinumMid = Color(0xFF94A3B8)
val PlatinumDark = Color(0xFF475569)
val TitaniumSteel = Color(0xFF1E293B)

val ChampagneGoldLight = Color(0xFFFEF3C7)
val ChampagneGoldShine = Color(0xFFFDE68A)
val ChampagneGoldMid = Color(0xFFF59E0B)
val ChampagneGoldBronze = Color(0xFFB45309)
val ChampagneGoldDeep = Color(0xFF78350F)

val ObsidianMedallion = Color(0xFF0F141E)
val ObsidianBorder = Color(0xFF1E2638)

@Composable
fun MetallicLuxuryLogo(
  modifier: Modifier = Modifier,
  size: Dp = 120.dp,
  withGleamAnimation: Boolean = true
) {
  val infiniteTransition = rememberInfiniteTransition(label = "metallicGleam")
  val gleamOffset by infiniteTransition.animateFloat(
    initialValue = -1f,
    targetValue = 2f,
    animationSpec = infiniteRepeatable(
      animation = tween(3200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "gleamOffset"
  )

  Box(
    modifier = modifier.size(size),
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.size(size)) {
      val w = this.size.width
      val h = this.size.height
      val center = Offset(w / 2f, h / 2f)

      // 1. Dark Brushed Obsidian Medallion Base
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0xFF161F2E),
            Color(0xFF0F1622),
            Color(0xFF090D15)
          ),
          center = center,
          radius = w * 0.48f
        ),
        radius = w * 0.48f,
        center = center
      )

      // 2. Outer Chiseled Platinum Bevel Ring
      val bevelBrush = Brush.sweepGradient(
        listOf(
          PlatinumHighlight,
          PlatinumMid,
          PlatinumLight,
          PlatinumDark,
          PlatinumHighlight,
          ChampagneGoldShine,
          ChampagneGoldMid,
          PlatinumMid,
          PlatinumHighlight
        ),
        center = center
      )
      drawCircle(
        brush = bevelBrush,
        radius = w * 0.46f,
        center = center,
        style = Stroke(width = w * 0.035f)
      )

      // Inner Grooved Recess Line
      drawCircle(
        color = Color(0xFF0A0E17),
        radius = w * 0.43f,
        center = center,
        style = Stroke(width = w * 0.015f)
      )

      // 3. Left Stream Arc - Platinum & Brushed Titanium
      val platinumPath = Path().apply {
        val rOuter = w * 0.32f
        val rInner = w * 0.18f
        arcTo(
          rect = Size(rOuter * 2, rOuter * 2).let { s ->
            androidx.compose.ui.geometry.Rect(center - Offset(rOuter, rOuter), s)
          },
          startAngleDegrees = 110f,
          sweepAngleDegrees = 180f,
          forceMoveTo = true
        )
      }
      drawPath(
        path = platinumPath,
        brush = Brush.linearGradient(
          colors = listOf(PlatinumHighlight, PlatinumLight, PlatinumMid, PlatinumDark),
          start = Offset(w * 0.2f, h * 0.2f),
          end = Offset(w * 0.7f, h * 0.7f)
        ),
        style = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
      )

      // 4. Right Stream Arc - Champagne Gold & Polished Bronze
      val goldPath = Path().apply {
        val rOuter = w * 0.32f
        arcTo(
          rect = Size(rOuter * 2, rOuter * 2).let { s ->
            androidx.compose.ui.geometry.Rect(center - Offset(rOuter, rOuter), s)
          },
          startAngleDegrees = 290f,
          sweepAngleDegrees = 180f,
          forceMoveTo = true
        )
      }
      drawPath(
        path = goldPath,
        brush = Brush.linearGradient(
          colors = listOf(ChampagneGoldLight, ChampagneGoldMid, ChampagneGoldBronze, ChampagneGoldDeep),
          start = Offset(w * 0.8f, h * 0.3f),
          end = Offset(w * 0.3f, h * 0.8f)
        ),
        style = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
      )

      // 5. Interlocking Flow Chevrons
      // Top Arrow Head (Platinum)
      val arrow1 = Path().apply {
        moveTo(w * 0.58f, h * 0.26f)
        lineTo(w * 0.68f, h * 0.36f)
        lineTo(w * 0.58f, h * 0.41f)
        lineTo(w * 0.61f, h * 0.36f)
        close()
      }
      drawPath(
        path = arrow1,
        brush = Brush.linearGradient(
          colors = listOf(PlatinumHighlight, PlatinumMid),
          start = Offset(w * 0.55f, h * 0.26f),
          end = Offset(w * 0.68f, h * 0.41f)
        )
      )

      // Bottom Arrow Head (Champagne Gold)
      val arrow2 = Path().apply {
        moveTo(w * 0.42f, h * 0.74f)
        lineTo(w * 0.32f, h * 0.64f)
        lineTo(w * 0.42f, h * 0.59f)
        lineTo(w * 0.39f, h * 0.64f)
        close()
      }
      drawPath(
        path = arrow2,
        brush = Brush.linearGradient(
          colors = listOf(ChampagneGoldShine, ChampagneGoldBronze),
          start = Offset(w * 0.45f, h * 0.74f),
          end = Offset(w * 0.32f, h * 0.59f)
        )
      )

      // 6. Central Precision Diamond Facet
      val diamond = Path().apply {
        moveTo(center.x, center.y - w * 0.08f)
        lineTo(center.x + w * 0.08f, center.y)
        lineTo(center.x, center.y + w * 0.08f)
        lineTo(center.x - w * 0.08f, center.y)
        close()
      }
      drawPath(
        path = diamond,
        brush = Brush.linearGradient(
          colors = listOf(PlatinumHighlight, PlatinumLight, PlatinumMid),
          start = Offset(center.x - w * 0.08f, center.y - w * 0.08f),
          end = Offset(center.x + w * 0.08f, center.y + w * 0.08f)
        )
      )
      drawPath(
        path = diamond,
        color = ChampagneGoldShine,
        style = Stroke(width = w * 0.015f)
      )

      // 7. Dynamic Specular Light Gleam passing over emblem
      if (withGleamAnimation) {
        val sweepX = w * gleamOffset
        drawLine(
          brush = Brush.linearGradient(
            colors = listOf(
              Color.Transparent,
              Color.White.copy(alpha = 0.45f),
              Color.Transparent
            ),
            start = Offset(sweepX - w * 0.2f, 0f),
            end = Offset(sweepX + w * 0.2f, h)
          ),
          start = Offset(sweepX - w * 0.15f, 0f),
          end = Offset(sweepX + w * 0.15f, h),
          strokeWidth = w * 0.15f
        )
      }

      // Micro Specular Star Reflection at Center
      drawCircle(
        color = Color.White,
        radius = w * 0.02f,
        center = center
      )
    }
  }
}
