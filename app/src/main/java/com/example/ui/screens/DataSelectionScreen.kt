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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TransferRepository
import com.example.model.AiOptimizationReport
import com.example.model.CategorySummary
import com.example.model.ContentCategory
import com.example.model.DevicePeer
import com.example.ui.components.ContentCategorySelectionCard
import com.example.ui.components.FlowTopBar
import com.example.ui.theme.FlowAccentCyan
import com.example.ui.theme.FlowAccentEmerald
import com.example.ui.theme.FlowDarkBackground
import com.example.ui.theme.FlowDeepNavy
import com.example.ui.theme.FlowNavy800

@Composable
fun DataSelectionScreen(
  categories: List<CategorySummary>,
  peer: DevicePeer?,
  aiReport: AiOptimizationReport?,
  isAiModalVisible: Boolean,
  isOsLimitationSheetVisible: Boolean,
  onToggleCategory: (ContentCategory) -> Unit,
  onSelectAll: (Boolean) -> Unit,
  onSmartRecommendations: () -> Unit,
  onRunAiOptimization: () -> Unit,
  onApplyAiCleanup: () -> Unit,
  onCloseAiModal: () -> Unit,
  onToggleOsLimitationSheet: (Boolean) -> Unit,
  onStartTransfer: () -> Unit,
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val selectedCategories = categories.filter { it.isSelected }
  val totalFiles = selectedCategories.sumOf { it.itemCount }
  val totalBytes = selectedCategories.sumOf { it.totalSizeBytes }

  Scaffold(
    topBar = {
      FlowTopBar(
        title = "Choose Content",
        subtitle = "To: ${peer?.name ?: "Target Device"}",
        showBack = true,
        onBackClick = onBackClick
      )
    },
    bottomBar = {
      // Floating Bottom Action Bar
      Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Total Selected",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "${TransferRepository.formatBytes(totalBytes)} • $totalFiles items",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = FlowAccentCyan
              )
            }

            Button(
              onClick = onStartTransfer,
              enabled = selectedCategories.isNotEmpty(),
              colors = ButtonDefaults.buttonColors(
                containerColor = FlowAccentCyan,
                contentColor = FlowDeepNavy
              ),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .height(48.dp)
                .testTag("start_transfer_button")
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Start Transfer", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
            }
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
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))
        // One-Tap Presets Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FilterChip(
            selected = categories.all { it.isSelected },
            onClick = { onSelectAll(true) },
            label = { Text("Transfer Everything", fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = FlowAccentCyan.copy(alpha = 0.2f),
              selectedLabelColor = FlowAccentCyan
            ),
            modifier = Modifier.testTag("preset_transfer_everything")
          )
          FilterChip(
            selected = false,
            onClick = { onSmartRecommendations() },
            label = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp), tint = FlowAccentEmerald)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Smart Picks", fontSize = 12.sp)
              }
            },
            modifier = Modifier.testTag("preset_smart_picks")
          )
          FilterChip(
            selected = false,
            onClick = { onSelectAll(false) },
            label = { Text("Deselect All", fontSize = 12.sp) },
            modifier = Modifier.testTag("preset_deselect_all")
          )
        }
      }

      // AI Transfer Assistant Feature Banner
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.Transparent),
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onRunAiOptimization() }
            .testTag("ai_assistant_banner")
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.horizontalGradient(
                  colors = listOf(
                    FlowDeepNavy,
                    Color(0xFF132B50),
                    Color(0xFF0F3E48)
                  )
                )
              )
              .border(
                1.dp,
                FlowAccentCyan.copy(alpha = 0.4f),
                RoundedCornerShape(16.dp)
              )
              .padding(16.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              Surface(
                shape = CircleShape,
                color = FlowAccentCyan.copy(alpha = 0.2f),
                modifier = Modifier.size(44.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "AI Transfer Assistant",
                    tint = FlowAccentCyan,
                    modifier = Modifier.size(24.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.width(14.dp))

              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "AI Transfer Assistant",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "Analyze media, detect duplicates & save up to 4.8 GB",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color.White.copy(alpha = 0.8f)
                )
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = FlowAccentCyan,
                modifier = Modifier.padding(start = 6.dp)
              ) {
                Text(
                  text = "Analyze",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = FlowDeepNavy,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }
      }

      // OS Limitation Notice Pill Button
      item {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleOsLimitationSheet(true) }
            .testTag("os_limitation_info_button")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = null,
              tint = FlowAccentCyan,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Operating System Compatibility Matrix",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.weight(1f)
            )
            Text(
              text = "View info",
              style = MaterialTheme.typography.labelSmall,
              color = FlowAccentCyan
            )
          }
        }
      }

      item {
        Text(
          text = "Categories (${selectedCategories.size}/${categories.size})",
          style = MaterialTheme.typography.labelLarge,
          fontWeight = FontWeight.Bold
        )
      }

      // Categorized Content Cards
      items(categories) { categorySummary ->
        ContentCategorySelectionCard(
          categorySummary = categorySummary,
          onToggle = { onToggleCategory(categorySummary.category) }
        )
      }

      item {
        Spacer(modifier = Modifier.height(20.dp))
      }
    }
  }

  // AI Optimization Dialog / Modal
  if (isAiModalVisible && aiReport != null) {
    AlertDialog(
      onDismissRequest = onCloseAiModal,
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = FlowAccentCyan, modifier = Modifier.size(22.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("AI Transfer Assistant Analysis", style = MaterialTheme.typography.titleMedium)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = aiReport.aiRecommendationNote,
            style = MaterialTheme.typography.bodyMedium
          )

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Text(
                text = "Analysis Highlights:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text("• ${aiReport.duplicatesFoundCount} duplicate & forwarded media files found", style = MaterialTheme.typography.bodySmall)
              Text("• Large folders: ${aiReport.largeMediaFoldersFound.firstOrNull() ?: "None"}", style = MaterialTheme.typography.bodySmall)
              Text("• Estimated time saved: ~1m 45s", style = MaterialTheme.typography.bodySmall, color = FlowAccentEmerald)
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onApplyAiCleanup()
            onCloseAiModal()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = FlowAccentEmerald,
            contentColor = FlowDeepNavy
          ),
          modifier = Modifier.testTag("apply_ai_cleanup_button")
        ) {
          Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Exclude Duplicates", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        OutlinedButton(onClick = onCloseAiModal) {
          Text("Keep All")
        }
      }
    )
  }

  // OS Limitation Dialog
  if (isOsLimitationSheetVisible) {
    AlertDialog(
      onDismissRequest = { onToggleOsLimitationSheet(false) },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Info, contentDescription = null, tint = FlowAccentCyan, modifier = Modifier.size(22.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Cross-Platform Compatibility", style = MaterialTheme.typography.titleMedium)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Due to Android and iOS sandbox security, here is what can and cannot be imported directly:",
            style = MaterialTheme.typography.bodySmall
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text("✓ Photos & 4K Videos: 100% native gallery import", style = MaterialTheme.typography.bodySmall, color = FlowAccentEmerald)
          Text("✓ Contacts: Exported as standardized vCard (v4.0)", style = MaterialTheme.typography.bodySmall, color = FlowAccentEmerald)
          Text("✓ Documents, Music & Notes: 100% compatible", style = MaterialTheme.typography.bodySmall, color = FlowAccentEmerald)
          Text("✓ Calendar & Reminders: Universal iCal (.ics)", style = MaterialTheme.typography.bodySmall, color = FlowAccentEmerald)
          Text("⚠ iOS Sandboxed App Data & Passwords: OS-restricted. App data stays inside sandboxes; FlowTransfer provides export guide.", style = MaterialTheme.typography.bodySmall, color = Color(0xFFF59E0B))
        }
      },
      confirmButton = {
        Button(
          onClick = { onToggleOsLimitationSheet(false) },
          colors = ButtonDefaults.buttonColors(
            containerColor = FlowAccentCyan,
            contentColor = FlowDeepNavy
          )
        ) {
          Text("Got it")
        }
      }
    )
  }
}
