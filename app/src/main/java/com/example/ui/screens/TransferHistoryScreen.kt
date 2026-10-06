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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TransferRecordEntity
import com.example.data.TransferRepository
import com.example.ui.components.FlowTopBar
import com.example.ui.theme.FlowAccentCyan
import com.example.ui.theme.FlowAccentEmerald
import com.example.ui.theme.FlowDeepNavy
import com.example.ui.theme.FlowStatusError
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransferHistoryScreen(
  transfers: List<TransferRecordEntity>,
  onDeleteRecord: (Long) -> Unit,
  onClearAll: () -> Unit,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var showClearConfirm by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      FlowTopBar(
        title = "Transfer History",
        subtitle = "${transfers.size} completed migrations",
        showBack = true,
        onBackClick = onBackClick,
        actions = {
          if (transfers.isNotEmpty()) {
            IconButton(
              onClick = { showClearConfirm = true },
              modifier = Modifier.testTag("clear_history_icon_button")
            ) {
              Icon(Icons.Default.DeleteSweep, contentDescription = "Clear All")
            }
          }
        }
      )
    },
    containerColor = MaterialTheme.colorScheme.background,
    modifier = modifier
  ) { paddingValues ->
    if (transfers.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(64.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "No Past Transfers Yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
          .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        item {
          Spacer(modifier = Modifier.height(6.dp))
        }

        items(transfers, key = { it.id }) { item ->
          HistoryRecordCard(
            record = item,
            onDelete = { onDeleteRecord(item.id) }
          )
        }

        item {
          Spacer(modifier = Modifier.height(20.dp))
        }
      }
    }
  }

  if (showClearConfirm) {
    AlertDialog(
      onDismissRequest = { showClearConfirm = false },
      title = { Text("Clear All Transfer History?") },
      text = { Text("This will permanently remove all logs of past transfers from this device.") },
      confirmButton = {
        Button(
          onClick = {
            onClearAll()
            showClearConfirm = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = FlowStatusError),
          modifier = Modifier.testTag("confirm_clear_history_button")
        ) {
          Text("Clear All")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showClearConfirm = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun HistoryRecordCard(
  record: TransferRecordEntity,
  onDelete: () -> Unit
) {
  val dateFormatted = remember(record.timestamp) {
    val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    sdf.format(Date(record.timestamp))
  }

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    modifier = Modifier.fillMaxWidth().testTag("history_item_${record.id}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
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
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = FlowAccentEmerald,
                modifier = Modifier.size(20.dp)
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = record.peerDeviceName,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
            Text(
              text = dateFormatted,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surface
          ) {
            Text(
              text = "${record.transferMode} • ${record.totalFiles} files",
              style = MaterialTheme.typography.labelSmall,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = FlowAccentCyan.copy(alpha = 0.15f)
          ) {
            Text(
              text = TransferRepository.formatBytes(record.totalBytes),
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = FlowAccentCyan,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }

        Text(
          text = "${record.durationSeconds}s @ ${record.avgSpeedMbps.toInt()} MB/s",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}
