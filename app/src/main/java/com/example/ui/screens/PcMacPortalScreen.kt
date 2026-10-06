package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FlowTopBar
import com.example.ui.components.QrCodeVisualizer
import com.example.ui.theme.FlowAccentCyan
import com.example.ui.theme.FlowAccentEmerald
import com.example.ui.theme.FlowDeepNavy

@Composable
fun PcMacPortalScreen(
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Scaffold(
    topBar = {
      FlowTopBar(
        title = "PC & Mac Transfer",
        subtitle = "Zero Cable Browser Portal",
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
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          shape = CircleShape,
          color = FlowAccentCyan.copy(alpha = 0.15f),
          modifier = Modifier.size(68.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Computer,
              contentDescription = "PC & Mac",
              tint = FlowAccentCyan,
              modifier = Modifier.size(36.dp)
            )
          }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "Connect PC or Mac",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          textAlign = TextAlign.Center
        )
        Text(
          text = "Transfer files to and from Windows or macOS via any modern web browser without installing software.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 16.dp)
        )
      }

      // Web Portal URL Box
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
              text = "1. Open this URL on your PC or Mac browser:",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = FlowDeepNavy,
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "http://192.168.1.142:8080",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontFamily = FontFamily.Monospace,
                  fontWeight = FontWeight.Bold
                ),
                color = FlowAccentCyan,
                textAlign = TextAlign.Center,
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 12.dp)
              )
            }
          }
        }
      }

      // QR Code to scan with laptop camera
      item {
        Text(
          text = "2. Or scan this code with laptop / tablet:",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        QrCodeVisualizer(payloadText = "http://192.168.1.142:8080")
      }

      // Direct Drag & Drop Features
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = "Supported PC & Mac Capabilities:",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
            Text("• High-speed drag and drop full photo albums & 4K videos", style = MaterialTheme.typography.bodySmall)
            Text("• Download entire contacts library as single vCard (.vcf)", style = MaterialTheme.typography.bodySmall)
            Text("• Upload zip archives and folder structures directly to Phone", style = MaterialTheme.typography.bodySmall)
            Text("• Encrypted local connection entirely on your Wi-Fi router", style = MaterialTheme.typography.bodySmall, color = FlowAccentEmerald)
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}
