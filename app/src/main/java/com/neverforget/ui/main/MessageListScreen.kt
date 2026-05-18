package com.neverforget.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neverforget.ui.main.MessageListViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageListScreen(viewModel: MessageListViewModel = hiltViewModel()) {
    val conversations by viewModel.conversations.collectAsState()
    val filterApp by viewModel.filterApp.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
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

        // 会话列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(conversations, key = { "${it.app}|${it.name}" }) { conv ->
                ConversationCard(conv)
            }
        }
    }
}

@Composable
private fun ConversationCard(conv: ConversationGroup) {
    val allSummarized = conv.summarizedCount >= conv.messageCount && conv.messageCount > 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clickable { /* TODO: 展开会话消息详情 */ },
        colors = CardDefaults.cardColors(
            containerColor = when {
                allSummarized -> Color(0xFFF5F5F5) // 灰色底 = 已摘要
                conv.type == "private" -> Color(0xFFE3F2FD)
                else -> Color(0xFFE8F5E9)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // 标题行
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (conv.type == "group") Icons.Default.Groups else Icons.Default.Person,
                    contentDescription = null,
                    tint = if (conv.type == "private")
                        Color(0xFF1976D2)
                    else
                        Color(0xFF388E3C),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = conv.name,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.weight(1f))
                if (allSummarized) {
                    Text(
                        text = "已摘要",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF4CAF50)
                    )
                } else if (conv.summarizedCount > 0) {
                    Text(
                        text = "${conv.summarizedCount}/${conv.messageCount} 已摘要",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFFF9800)
                    )
                }
                Text(
                    text = "${conv.messageCount}条",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(4.dp))

            // 最近消息预览（最多3条）
            conv.latestMessages.reversed().forEach { msg ->
                Text(
                    text = "${msg.senderName}：${msg.content}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }

            Spacer(Modifier.height(4.dp))

            // 时间
            Text(
                text = formatTime(conv.latestTime),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "刚刚"
        diff < 3_600_000 -> "${diff / 60_000}分钟前"
        diff < 86_400_000 -> "${diff / 3_600_000}小时前"
        else -> {
            val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}
