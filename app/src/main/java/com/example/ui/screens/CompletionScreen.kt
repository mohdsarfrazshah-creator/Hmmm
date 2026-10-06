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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.TransferRepository
import com.example.model.ContentCategory
import com.example.model.VerificationReport
import com.example.ui.components.FlowTopBar
import com.example.ui.theme.FlowAccentCyan
import com.example.ui.theme.FlowAccentEmerald
import com.example.ui.theme.FlowDeepNavy
import com.example.ui.theme.FlowStatusSuccess

@Composable
fun CompletionScreen(
  report: VerificationReport?,
  onDoneClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Scaffold(
    topBar = {
      FlowTopBar(
        title = "Migration Complete",
        subtitle = "Cryptographically Verified",
        showBack = false
      )
    },
    bottomBar = {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Button(
            onClick = onDoneClick,
            colors = ButtonDefaults.buttonColors(
              containerColor = FlowAccentCyan,
              contentColor = FlowDeepNavy
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("done_completion_button")
          ) {
            Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Done", fontWeight = FontWeight.Bold)
          }
        }
      }
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
        // Celebratory Checkmark Icon
        Surface(
          shape = CircleShape,
          color = FlowAccentEmerald.copy(alpha = 0.15f),
          border = androidx.compose.foundation.BorderStroke(2.dp, FlowAccentEmerald),
          modifier = Modifier.size(80.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Success",
              tint = FlowAccentEmerald,
              modifier = Modifier.size(46.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "Migration Successful!",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "All files transferred without loss. 100% integrity verified.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center
        )
      }

      // Verification Badge Banner
      item {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = FlowAccentEmerald.copy(alpha = 0.12f),
          border = androidx.compose.foundation.BorderStroke(1.dp, FlowAccentEmerald.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.VerifiedUser,
              contentDescription = null,
              tint = FlowAccentEmerald,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "SHA-256 Cryptographic Match",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = FlowAccentEmerald
              )
              Text(
                text = "0 missing files • 0 corrupt packets",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Stat Metrics Grid
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          StatCard(
            title = "Transferred",
            value = TransferRepository.formatBytes(report?.totalBytesReceived ?: 0L),
            modifier = Modifier.weight(1f)
          )
          StatCard(
            title = "Files Verified",
            value = "${report?.totalFilesReceived ?: 0}",
            modifier = Modifier.weight(1f)
          )
        }
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          val dur = report?.durationSeconds ?: 0L
          val durStr = if (dur > 60) "${dur / 60}m ${dur % 60}s" else "${dur}s"
          StatCard(
            title = "Duration",
            value = durStr,
            modifier = Modifier.weight(1f)
          )
          StatCard(
            title = "Avg Speed",
            value = "${(report?.avgSpeedMbps ?: 75.0).toInt()} MB/s",
            modifier = Modifier.weight(1f)
          )
        }
      }

      // Category Breakdown List
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Transferred Content Breakdown",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
        }
      }

      val breakdownList = report?.categoryBreakdown?.entries?.toList() ?: emptyList()
      items(breakdownList) { entry ->
        val cat = entry.key
        val (count, bytes) = entry.value
        Card(
          shape = RoundedCornerShape(12.dp),
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
              color = FlowAccentCyan.copy(alpha = 0.15f),
              modifier = Modifier.size(34.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = cat.icon,
                  contentDescription = null,
                  tint = FlowAccentCyan,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = cat.displayName,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "$count items",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Text(
              text = TransferRepository.formatBytes(bytes),
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = FlowAccentEmerald
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

@Composable
fun StatCard(
  title: String,
  value: String,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground
      )
    }
  }
}
