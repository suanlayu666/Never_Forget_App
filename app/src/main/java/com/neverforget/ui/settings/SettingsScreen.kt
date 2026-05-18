package com.neverforget.ui.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.neverforget.accessibility.MessageAccessibilityService
import com.neverforget.floating.FloatingWindowService

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val aiApiUrl by viewModel.aiApiUrl.collectAsState()
    val aiApiKey by viewModel.aiApiKey.collectAsState()
    val aiModel by viewModel.aiModel.collectAsState()
    val captureWechat by viewModel.captureWechat.collectAsState()
    val captureWeWork by viewModel.captureWeWork.collectAsState()
    val captureQq by viewModel.captureQq.collectAsState()
    val autoSummaryEnabled by viewModel.autoSummaryEnabled.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }
    val autoSummaryInterval by viewModel.autoSummaryInterval.collectAsState()
    val autoSummaryMessageCount by viewModel.autoSummaryMessageCount.collectAsState()
    val notificationEnabled by viewModel.notificationEnabled.collectAsState()

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { _ ->
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ========== AI 配置 ==========
        item {
            SectionHeader(title = "AI 配置")
            Column(modifier = Modifier.padding(top = 8.dp)) {
                OutlinedTextField(
                    value = aiApiUrl,
                    onValueChange = { viewModel.setAiApiUrl(it) },
                    label = { Text("API 地址") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = aiApiKey,
                    onValueChange = { viewModel.setAiApiKey(it) },
                    label = { Text("API Key") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = aiModel,
                    onValueChange = { viewModel.setAiModel(it) },
                    label = { Text("模型名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ========== 悬浮窗 ==========
        item {
            SectionHeader(title = "悬浮窗")
            Column(modifier = Modifier.padding(top = 8.dp)) {
                // 检查权限
                val hasOverlayPermission = Settings.canDrawOverlays(context)
                if (hasOverlayPermission) {
                    Text(
                        text = "状态：悬浮窗已授权 ✓",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    Text(
                        text = "需要开启悬浮窗权限才能在其他应用上方显示",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(4.dp))
                    Button(
                        onClick = {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            )
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("开启悬浮窗权限")
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(context, FloatingWindowService::class.java)
                            ContextCompat.startForegroundService(context, intent)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = hasOverlayPermission
                    ) {
                        Text("启动悬浮窗")
                    }
                    OutlinedButton(
                        onClick = {
                            context.stopService(Intent(context, FloatingWindowService::class.java))
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("停止悬浮窗")
                    }
                }
            }
        }

        // ========== 无障碍服务 ==========
        item {
            SectionHeader(title = "无障碍服务（屏幕扫描）")
            Column(modifier = Modifier.padding(top = 8.dp)) {
                val a11yEnabled = MessageAccessibilityService.isRunning
                if (a11yEnabled) {
                    Text(
                        text = "状态：无障碍服务已开启 ✓",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    Text(
                        text = "需要开启无障碍服务才能扫描微信/企微屏幕内容",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(4.dp))
                    Button(
                        onClick = { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("开启无障碍服务")
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "开启后找到「NeverForget」并打开开关",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        // ========== 抓取设置 ==========
        item {
            SectionHeader(title = "抓取设置")
            Column(modifier = Modifier.padding(top = 8.dp)) {
                SwitchRow(
                    label = "微信消息抓取",
                    description = "捕获微信聊天消息",
                    checked = captureWechat,
                    onCheckedChange = { viewModel.setCaptureWechat(it) }
                )
                SwitchRow(
                    label = "企微消息抓取",
                    description = "捕获企业微信聊天消息",
                    checked = captureWeWork,
                    onCheckedChange = { viewModel.setCaptureWeWork(it) }
                )
                SwitchRow(
                    label = "QQ 消息抓取",
                    description = "捕获 QQ 聊天消息",
                    checked = captureQq,
                    onCheckedChange = { viewModel.setCaptureQq(it) }
                )
            }
        }

        // ========== 自动摘要 ==========
        item {
            SectionHeader(title = "自动摘要")
            Column(modifier = Modifier.padding(top = 8.dp)) {
                SwitchRow(
                    label = "自动摘要",
                    description = "开启后自动生成消息摘要",
                    checked = autoSummaryEnabled,
                    onCheckedChange = { viewModel.setAutoSummaryEnabled(it) }
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = autoSummaryMessageCount.toString(),
                    onValueChange = { it.toIntOrNull()?.let { cnt -> viewModel.setAutoSummaryMessageCount(cnt) } },
                    label = { Text("每 N 条消息后触发") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = autoSummaryInterval.toString(),
                    onValueChange = { it.toIntOrNull()?.let { min -> viewModel.setAutoSummaryInterval(min) } },
                    label = { Text("每 N 小时后触发") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ========== 通知 ==========
        item {
            SectionHeader(title = "通知")
            Column(modifier = Modifier.padding(top = 8.dp)) {
                SwitchRow(
                    label = "摘要生成通知",
                    description = "新摘要生成后推送通知",
                    checked = notificationEnabled,
                    onCheckedChange = { viewModel.setNotificationEnabled(it) }
                )
            }
        }

        // ========== 数据管理 ==========
        item {
            SectionHeader(title = "数据管理")
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Button(
                    onClick = { viewModel.createTestMessages() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("添加 8 条测试消息（待摘要）")
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* TODO: 导出功能 */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("导出数据")
                    }
                    Button(
                        onClick = {
                            viewModel.deleteAllMessages()
                            viewModel.deleteAllSummaries()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("清除全部")
                    }
                }
            }
        }

        // 底部留白
        item { Spacer(Modifier.height(64.dp)) }
    }
    } // Scaffold end
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun SwitchRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
