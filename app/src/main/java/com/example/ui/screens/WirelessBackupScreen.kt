package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FlowTopBar
import com.example.ui.theme.FlowAccentCyan
import com.example.ui.theme.FlowAccentEmerald
import com.example.ui.theme.FlowDeepNavy

@Composable
fun WirelessBackupScreen(
  isBackupInProgress: Boolean,
  backupProgress: Float,
  isBackupCompleted: Boolean,
  onStartBackup: () -> Unit,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Scaffold(
    topBar = {
      FlowTopBar(
        title = "Wireless Backup",
        subtitle = "Local Encrypted Snapshot",
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
      verticalArrangement = Arrangement.spacedBy(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      item {
        Spacer(modifier = Modifier.height(10.dp))
        Surface(
          shape = CircleShape,
          color = FlowAccentEmerald.copy(alpha = 0.15f),
          modifier = Modifier.size(68.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Backup,
              contentDescription = "Wireless Backup",
              tint = FlowAccentEmerald,
              modifier = Modifier.size(36.dp)
            )
          }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "Local Wireless Backup",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          textAlign = TextAlign.Center
        )
        Text(
          text = "Automatically snapshot your media, contacts and files to your local network storage or PC/Mac server without cloud subscriptions.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 16.dp)
        )
      }

      // Backup Action & Progress Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Full Device Snapshot",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = FlowAccentCyan.copy(alpha = 0.15f)
              ) {
                Text(
                  text = "38.2 GB",
                  style = MaterialTheme.typography.labelSmall,
                  color = FlowAccentCyan,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Target: Synology NAS / Home PC Server (192.168.1.50)",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isBackupInProgress) {
              Column {
                LinearProgressIndicator(
                  progress = { backupProgress },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                  color = FlowAccentEmerald,
                  trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "Backing up photos & contacts...",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Text(
                    text = "${(backupProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = FlowAccentEmerald,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            } else if (isBackupCompleted) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = FlowAccentEmerald.copy(alpha = 0.15f),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.CheckCircle, contentDescription = null, tint = FlowAccentEmerald, modifier = Modifier.size(20.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Backup snapshot created and verified successfully!",
                    style = MaterialTheme.typography.labelSmall,
                    color = FlowAccentEmerald,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            } else {
              Button(
                onClick = onStartBackup,
                colors = ButtonDefaults.buttonColors(
                  containerColor = FlowAccentEmerald,
                  contentColor = FlowDeepNavy
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp)
                  .testTag("start_backup_button")
              ) {
                Icon(Icons.Default.Backup, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Local Backup Now", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // Backup Features Breakdown
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
              text = "Wireless Backup Highlights",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Storage, contentDescription = null, tint = FlowAccentCyan, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Text("Zero third-party cloud storage dependency", style = MaterialTheme.typography.bodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Schedule, contentDescription = null, tint = FlowAccentCyan, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Text("Nightly automatic differential backup when charging", style = MaterialTheme.typography.bodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Folder, contentDescription = null, tint = FlowAccentCyan, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(10.dp))
              Text("Standard open formats (JPG, MP4, vCard, ICS)", style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }
}
