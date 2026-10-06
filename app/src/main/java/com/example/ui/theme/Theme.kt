package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = FlowAccentCyan,
  onPrimary = FlowDeepNavy,
  primaryContainer = FlowNavy700,
  onPrimaryContainer = FlowAccentCyanLight,
  secondary = FlowAccentEmerald,
  onSecondary = FlowDeepNavy,
  secondaryContainer = FlowDarkSurfaceVariant,
  onSecondaryContainer = FlowAccentEmeraldLight,
  tertiary = FlowPrimaryBlue,
  background = FlowDarkBackground,
  onBackground = FlowTextPrimaryDark,
  surface = FlowDarkSurface,
  onSurface = FlowTextPrimaryDark,
  surfaceVariant = FlowDarkSurfaceVariant,
  onSurfaceVariant = FlowTextSecondaryDark,
  outline = FlowDarkBorder
)

private val LightColorScheme = lightColorScheme(
  primary = FlowPrimaryBlue,
  onPrimary = Color.White,
  primaryContainer = Color(0xFFDBEAFE),
  onPrimaryContainer = Color(0xFF1E3A8A),
  secondary = FlowAccentEmerald,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFD1FAE5),
  onSecondaryContainer = Color(0xFF065F46),
  tertiary = FlowAccentCyan,
  background = FlowLightBackground,
  onBackground = FlowTextPrimaryLight,
  surface = FlowLightSurface,
  onSurface = FlowTextPrimaryLight,
  surfaceVariant = FlowLightSurfaceVariant,
  onSurfaceVariant = FlowTextSecondaryLight,
  outline = FlowLightBorder
)

@Composable
fun FlowTransferTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Use our intentional branded palette by default
  content: @Composable () -> Unit
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
