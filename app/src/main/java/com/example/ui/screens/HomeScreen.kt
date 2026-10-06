package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.TabletMac
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TransferRecordEntity
import com.example.data.TransferRepository
import com.example.model.TransferMode
import com.example.ui.components.FlowTopBar
import com.example.ui.theme.FlowAccentCyan
import com.example.ui.theme.FlowAccentEmerald
import com.example.ui.theme.FlowDeepNavy
import com.example.ui.theme.FlowNavy700
import com.example.ui.theme.FlowNavy800
import com.example.ui.theme.FlowPrimaryBlue

@Composable
fun HomeScreen(
  onSendClick: () -> Unit,
  onReceiveClick: () -> Unit,
  onPcMacClick: () -> Unit,
  onBackupClick: () -> Unit,
  onHistoryClick: () -> Unit,
  onSettingsClick: () -> Unit,
  recentTransfers: List<TransferRecordEntity>,
  completedCount: Int,
  totalBytes: Long,
  selectedTab: Int,
  onTabSelected: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  Scaffold(
    topBar = {
      FlowTopBar(
        title = "FlowTransfer",
        subtitle = "Move Your Digital Life",
        showBack = false
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("bottom_nav_bar")
      ) {
        NavigationBarItem(
          icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
          label = { Text("Home") },
          selected = selectedTab == 0,
          onClick = { onTabSelected(0) },
          colors = NavigationBarItemDefaults.colors(
            indicatorColor = FlowAccentCyan.copy(alpha = 0.2f),
            selectedIconColor = FlowAccentCyan,
            selectedTextColor = FlowAccentCyan
          )
        )
        NavigationBarItem(
          icon = { Icon(Icons.Default.Backup, contentDescription = "Backup") },
          label = { Text("Backup") },
          selected = selectedTab == 1,
          onClick = { onTabSelected(1) },
          colors = NavigationBarItemDefaults.colors(
            indicatorColor = FlowAccentCyan.copy(alpha = 0.2f),
            selectedIconColor = FlowAccentCyan,
            selectedTextColor = FlowAccentCyan
          )
        )
        NavigationBarItem(
          icon = { Icon(Icons.Default.History, contentDescription = "History") },
          label = { Text("History") },
          selected = selectedTab == 2,
          onClick = { onTabSelected(2) },
          colors = NavigationBarItemDefaults.colors(
            indicatorColor = FlowAccentCyan.copy(alpha = 0.2f),
            selectedIconColor = FlowAccentCyan,
            selectedTextColor = FlowAccentCyan
          )
        )
        NavigationBarItem(
          icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
          label = { Text("Settings") },
          selected = selectedTab == 3,
          onClick = { onTabSelected(3) },
          colors = NavigationBarItemDefaults.colors(
            indicatorColor = FlowAccentCyan.copy(alpha = 0.2f),
            selectedIconColor = FlowAccentCyan,
            selectedTextColor = FlowAccentCyan
          )
        )
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
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))
        // Network Status Badge Bar
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(
                shape = CircleShape,
                color = FlowAccentEmerald,
                modifier = Modifier.size(8.dp)
              ) {}
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Wi-Fi 6 Direct Ready",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
            Text(
              text = "Max ~95 MB/s",
              style = MaterialTheme.typography.labelSmall,
              color = FlowAccentCyan,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      // Main Send / Receive Hero Action Cards
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // Send Device Card
          Card(
            modifier = Modifier
              .weight(1f)
              .height(175.dp)
              .clip(RoundedCornerShape(20.dp))
              .clickable { onSendClick() }
              .testTag("send_device_button"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
          ) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    colors = listOf(
                      FlowPrimaryBlue,
                      Color(0xFF1E3A8A)
                    )
                  )
                )
                .padding(16.dp)
            ) {
              Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
              ) {
                Surface(
                  shape = CircleShape,
                  color = Color.White.copy(alpha = 0.2f),
                  modifier = Modifier.size(44.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Default.ArrowUpward,
                      contentDescription = "Send Data",
                      tint = Color.White,
                      modifier = Modifier.size(24.dp)
                    )
                  }
                }

                Column {
                  Text(
                    text = "Send Data",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "This is the Old Device",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                  )
                }
              }
            }
          }

          // Receive Device Card
          Card(
            modifier = Modifier
              .weight(1f)
              .height(175.dp)
              .clip(RoundedCornerShape(20.dp))
              .clickable { onReceiveClick() }
              .testTag("receive_device_button"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
          ) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    colors = listOf(
                      Color(0xFF065F46),
                      Color(0xFF047857)
                    )
                  )
                )
                .padding(16.dp)
            ) {
              Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
              ) {
                Surface(
                  shape = CircleShape,
                  color = Color.White.copy(alpha = 0.2f),
                  modifier = Modifier.size(44.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Default.ArrowDownward,
                      contentDescription = "Receive Data",
                      tint = Color.White,
                      modifier = Modifier.size(24.dp)
                    )
                  }
                }

                Column {
                  Text(
                    text = "Receive Data",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                  )
                  Spacer(modifier = Modifier.height(2.dp))
                  Text(
                    text = "This is the New Device",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                  )
                }
              }
            }
          }
        }
      }

      // Fast Cross-Platform Migration Matrix Pill
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Universal Cross-Platform Migration",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              MigrationPill("Android", "Android", Icons.Default.PhoneAndroid, Icons.Default.PhoneAndroid)
              MigrationPill("iPhone", "iPhone", Icons.Default.PhoneIphone, Icons.Default.PhoneIphone)
              MigrationPill("Android", "iPhone", Icons.Default.PhoneAndroid, Icons.Default.PhoneIphone)
              MigrationPill("Tablet", "Phone", Icons.Default.TabletMac, Icons.Default.PhoneAndroid)
            }
          }
        }
      }

      // Quick Bridges: PC & Mac Support and Wireless Backup
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // PC & Mac Card
          Card(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(16.dp))
              .clickable { onPcMacClick() }
              .testTag("pc_mac_portal_button"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = CircleShape,
                color = FlowAccentCyan.copy(alpha = 0.15f),
                modifier = Modifier.size(36.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.Computer,
                    contentDescription = "PC & Mac",
                    tint = FlowAccentCyan,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "PC & Mac",
                  style = MaterialTheme.typography.labelLarge,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Web Portal",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }

          // Wireless Backup Card
          Card(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(16.dp))
              .clickable { onBackupClick() }
              .testTag("wireless_backup_button"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = CircleShape,
                color = FlowAccentEmerald.copy(alpha = 0.15f),
                modifier = Modifier.size(36.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.Backup,
                    contentDescription = "Wireless Backup",
                    tint = FlowAccentEmerald,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Backup",
                  style = MaterialTheme.typography.labelLarge,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "Snapshot",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }

      // Lifetime Migration Stats Bar
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
          ),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = TransferRepository.formatBytes(totalBytes),
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = FlowAccentCyan
              )
              Text(
                text = "Data Transferred",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Surface(
              modifier = Modifier
                .width(1.dp)
                .height(40.dp),
              color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            ) {}

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "$completedCount",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = FlowAccentEmerald
              )
              Text(
                text = "Devices Migrated",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Recent Transfers Section Header
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Recent Transfers",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "View All",
            style = MaterialTheme.typography.labelMedium,
            color = FlowAccentCyan,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onHistoryClick() }
          )
        }
      }

      // Recent transfers list
      if (recentTransfers.isEmpty()) {
        item {
          Text(
            text = "No transfers yet. Start your first migration above!",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 12.dp)
          )
        }
      } else {
        items(recentTransfers) { record ->
          RecentTransferItem(record = record)
        }
      }

      item {
        Spacer(modifier = Modifier.height(16.dp))
      }
    }
  }
}

@Composable
fun MigrationPill(
  from: String,
  to: String,
  fromIcon: androidx.compose.ui.graphics.vector.ImageVector,
  toIcon: androidx.compose.ui.graphics.vector.ImageVector
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = MaterialTheme.colorScheme.surface,
    modifier = Modifier.padding(horizontal = 2.dp)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(imageVector = fromIcon, contentDescription = null, modifier = Modifier.size(13.dp), tint = FlowAccentCyan)
      Icon(imageVector = Icons.Default.SyncAlt, contentDescription = null, modifier = Modifier.size(11.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
      Icon(imageVector = toIcon, contentDescription = null, modifier = Modifier.size(13.dp), tint = FlowAccentEmerald)
    }
  }
}

@Composable
fun RecentTransferItem(record: TransferRecordEntity) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = CircleShape,
        color = FlowAccentEmerald.copy(alpha = 0.15f),
        modifier = Modifier.size(40.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Verified",
            tint = FlowAccentEmerald,
            modifier = Modifier.size(22.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = record.peerDeviceName,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.SemiBold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = "${record.transferMode} • ${record.totalFiles} files • ${TransferRepository.formatBytes(record.totalBytes)}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Column(horizontalAlignment = Alignment.End) {
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = FlowAccentCyan.copy(alpha = 0.15f)
        ) {
          Text(
            text = "${record.avgSpeedMbps.toInt()} MB/s",
            style = MaterialTheme.typography.labelSmall,
            color = FlowAccentCyan,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }
  }
}
