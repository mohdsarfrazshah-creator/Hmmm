package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectionType
import com.example.ui.components.FlowTopBar
import com.example.ui.theme.FlowAccentCyan
import com.example.ui.theme.FlowAccentEmerald
import com.example.ui.theme.FlowDeepNavy

@Composable
fun SettingsScreen(
  isDarkTheme: Boolean,
  onThemeChanged: (Boolean) -> Unit,
  isE2eeEnabled: Boolean,
  onE2eeChanged: (Boolean) -> Unit,
  selectedLanguage: String,
  onLanguageChanged: (String) -> Unit,
  preferredConnection: ConnectionType,
  onConnectionChanged: (ConnectionType) -> Unit,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Scaffold(
    topBar = {
      FlowTopBar(
        title = "Settings",
        subtitle = "Preferences & Protocols",
        showBack = true,
        onBackClick = onBackClick
      )
    },
    containerColor = MaterialTheme.colorScheme.background,
    modifier = modifier
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Security & Encryption",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = FlowAccentCyan
        )
      }

      // Security Settings Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = CircleShape,
                  color = FlowAccentEmerald.copy(alpha = 0.15f),
                  modifier = Modifier.size(36.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = FlowAccentEmerald, modifier = Modifier.size(20.dp))
                  }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text("End-to-End Encryption", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                  Text("AES-GCM-256 Bit Cipher", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
              }

              Switch(
                checked = isE2eeEnabled,
                onCheckedChange = onE2eeChanged,
                colors = SwitchDefaults.colors(checkedThumbColor = FlowAccentEmerald, checkedTrackColor = FlowAccentEmerald.copy(alpha = 0.4f)),
                modifier = Modifier.testTag("switch_e2ee")
              )
            }
          }
        }
      }

      item {
        Text(
          text = "Preferred Transfer Protocol",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = FlowAccentCyan
        )
      }

      // Protocol Selection Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            ConnectionType.entries.forEach { conn ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { onConnectionChanged(conn) }
                  .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text(conn.displayName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                  Text("Speed: ${conn.estimatedSpeed} • ${conn.securityProtocol}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                RadioButton(
                  selected = preferredConnection == conn,
                  onClick = { onConnectionChanged(conn) },
                  colors = RadioButtonDefaults.colors(selectedColor = FlowAccentCyan)
                )
              }
            }
          }
        }
      }

      item {
        Text(
          text = "Appearance & Theme",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = FlowAccentCyan
        )
      }

      // Appearance Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(
                shape = CircleShape,
                color = FlowAccentCyan.copy(alpha = 0.15f),
                modifier = Modifier.size(36.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(Icons.Default.DarkMode, contentDescription = null, tint = FlowAccentCyan, modifier = Modifier.size(20.dp))
                }
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text("Dark Theme", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text(if (isDarkTheme) "Deep Blue & Cyan" else "Light Contrast", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
            }

            Switch(
              checked = isDarkTheme,
              onCheckedChange = onThemeChanged,
              colors = SwitchDefaults.colors(checkedThumbColor = FlowAccentCyan, checkedTrackColor = FlowAccentCyan.copy(alpha = 0.4f)),
              modifier = Modifier.testTag("switch_dark_theme")
            )
          }
        }
      }

      item {
        Text(
          text = "Language / Idioma",
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = FlowAccentCyan
        )
      }

      // Multi-Language Card
      item {
        val languages = listOf(
          "en" to "English (US)",
          "es" to "Español",
          "fr" to "Français",
          "de" to "Deutsch",
          "ja" to "日本語",
          "zh" to "中文 (简体)",
          "ar" to "العربية"
        )
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            languages.forEach { (code, name) ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { onLanguageChanged(code) }
                  .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(name, style = MaterialTheme.typography.bodyMedium)
                RadioButton(
                  selected = selectedLanguage == code,
                  onClick = { onLanguageChanged(code) },
                  colors = RadioButtonDefaults.colors(selectedColor = FlowAccentCyan)
                )
              }
            }
          }
        }
      }

      // About FlowTransfer Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "FlowTransfer",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Move Your Digital Life.",
              style = MaterialTheme.typography.labelSmall,
              color = FlowAccentCyan
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Version 2.4 (Build 2026) • Zero-Cloud Architecture\nFiles are transferred directly device-to-device without passing through external servers.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}
