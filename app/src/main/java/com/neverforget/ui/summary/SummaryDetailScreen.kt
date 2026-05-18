package com.neverforget.ui.summary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neverforget.data.local.entity.MessageEntity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryDetailScreen(
    summaryId: Long,
    onBack: () -> Unit,
    viewModel: SummaryViewModel = hiltViewModel()
) {
    var detail by remember { mutableStateOf<SummaryWithMessages?>(null) }
    var showOriginalMessages by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(summaryId) {
        scope.launch {
            detail = viewModel.getSummaryWithMessages(summaryId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("摘要详情") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        val data = detail
        if (data == null) {
            Text(
                text = "加载中...",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            )
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                // 标题
                Text(
                    text = data.summary.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                // 来源
                val appLabel = com.neverforget.util.AppLabel.displayName(data.summary.sourceApp)
                Text(
                    text = "来源：$appLabel · ${data.summary.conversationName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "时间：${data.summary.dateRange}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(16.dp))

                // 摘要正文：按 ## 分段
                data.summary.summaryContent.split("## ").filter { it.isNotBlank() }.forEach { section ->
                    val lines = section.split("\n")
                    val header = lines.firstOrNull()?.trim() ?: ""
                    val body = lines.drop(1).joinToString("\n").trim()

                    if (header.isNotBlank()) {
                        Text(
                            text = header,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                        )
                    }
                    if (body.isNotBlank()) {
                        body.split("\n").forEach { line ->
                            val trimmed = line.trim()
                            if (trimmed.isNotEmpty()) {
                                Text(
                                    text = trimmed,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = if (trimmed.startsWith("-") || trimmed[0].isDigit()) 8.dp else 0.dp, top = 2.dp, bottom = 2.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Divider()
                Spacer(Modifier.height(8.dp))

                // 原始消息展开按钮
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showOriginalMessages = !showOriginalMessages }
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = if (showOriginalMessages)
                            "▼ 原始消息（${data.originalMessages.size}条）"  else
                            "▶ 原始消息（${data.originalMessages.size}条）",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 可展开的原始消息
            item {
                AnimatedVisibility(visible = showOriginalMessages) {
                    Column {
                        data.originalMessages.forEach { msg ->
                            MessageItem(msg)
                            Divider(
                                modifier = Modifier.padding(vertical = 2.dp),
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageItem(msg: MessageEntity) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.shapes.small
            )
            .padding(8.dp)
    ) {
        Row {
            Text(
                text = msg.senderName,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = formatTimestamp(msg.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = msg.content,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
