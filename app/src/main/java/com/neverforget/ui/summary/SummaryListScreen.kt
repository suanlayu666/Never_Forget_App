package com.neverforget.ui.summary

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neverforget.data.local.entity.SummaryEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryListScreen(
    viewModel: SummaryViewModel = hiltViewModel(),
    onSummaryClick: (Long) -> Unit = {}
) {
    val summaries by viewModel.summaries.collectAsState()
    val filterApp by viewModel.filterApp.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val lastMessage by viewModel.lastMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showDialog by remember { mutableStateOf(false) }

    // 显示生成状态
    LaunchedEffect(lastMessage) {
        lastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // 会话多选对话框
    if (showDialog) {
        // 从未摘要消息中获取会话列表
        val conversations by viewModel.availableConversations.collectAsState()
        val selected = remember { mutableStateOf(setOf<String>()) }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("选择要摘要的会话（可多选）") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            showDialog = false
                            viewModel.generateSummary(null)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("自动选择（消息最多）", color = MaterialTheme.colorScheme.primary)
                    }
                    if (conversations.isEmpty()) {
                        Text("暂无会话，请先扫描或添加消息",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline)
                    }
                    for ((conv, _) in conversations) {
                        // 会话名已含 App 前缀，直接显示
                        val label = conv
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = conv in selected.value,
                                onCheckedChange = { checked ->
                                    selected.value = if (checked)
                                        selected.value + conv
                                    else
                                        selected.value - conv
                                }
                            )
                            Text(label, style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        val picked = selected.value.toList()
                        if (picked.isNotEmpty()) {
                            viewModel.generateSummary(picked)
                        } else {
                            viewModel.generateSummary(null)
                        }
                    }
                ) { Text("确定") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("取消") }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = "生成摘要",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // 筛选栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterApp == null,
                    onClick = { viewModel.setFilter(null) },
                    label = { Text("全部") }
                )
                FilterChip(
                    selected = filterApp == "wechat",
                    onClick = { viewModel.setFilter("wechat") },
                    label = { Text("微信") }
                )
                FilterChip(
                    selected = filterApp == "wework",
                    onClick = { viewModel.setFilter("wework") },
                    label = { Text("企微") }
                )
                FilterChip(
                    selected = filterApp == "qq",
                    onClick = { viewModel.setFilter("qq") },
                    label = { Text("QQ") }
                )
            }

            // 生成中提示
            if (isGenerating) {
                Text(
                    text = "AI 正在生成摘要...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            // 摘要卡片列表
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(summaries, key = { it.id }) { summary ->
                    SummaryCard(
                        summary = summary,
                        onClick = { onSummaryClick(summary.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(summary: SummaryEntity, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = summary.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                if (!summary.isRead) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.size(8.dp),
                        shape = MaterialTheme.shapes.small
                    ) {}
                }
            }

            Spacer(Modifier.height(4.dp))
            val appLabel = com.neverforget.util.AppLabel.displayName(summary.sourceApp)
            Text(
                text = "$appLabel · ${summary.dateRange}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = summary.summaryContent
                    .replace("## ", "")
                    .replace("\n", " ")
                    .take(120) + "...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = formatTime(summary.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 3_600_000 -> "${diff / 60_000}分钟前"
        diff < 86_400_000 -> "${diff / 3_600_000}小时前"
        else -> {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}
