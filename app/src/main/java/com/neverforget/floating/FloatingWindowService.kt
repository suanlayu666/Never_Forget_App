package com.neverforget.floating

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.neverforget.MainActivity
import com.neverforget.accessibility.MessageAccessibilityService
import com.neverforget.domain.SummaryEngine
import com.neverforget.repository.MessageRepository
import com.neverforget.repository.SummaryRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class FloatingWindowService : Service() {

    @Inject lateinit var messageRepository: MessageRepository
    @Inject lateinit var summaryRepository: SummaryRepository
    @Inject lateinit var summaryEngine: SummaryEngine

    private lateinit var windowManager: WindowManager
    private var bubbleView: View? = null
    private var panelView: LinearLayout? = null
    private var layoutParams: WindowManager.LayoutParams? = null
    private var isPanelShown = false
    private var isDragging = false
    private var updateJob: Job? = null
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var initialWindowX = 0
    private var initialWindowY = 0
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (bubbleView == null) {
            showBubble()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        scope.cancel()
        dismissBubble()
        dismissPanel()
        super.onDestroy()
    }

    private fun showBubble() {
        // 创建悬浮球
        val bubble = FrameLayout(this).apply {
            // 圆形蓝色背景
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#4A90D9"))
                setSize(dp(56), dp(56))
            }

            // "Never" 大文字 + "记" 小文字
            addView(TextView(this@FloatingWindowService).apply {
                text = "Never"
                textSize = 12f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER or Gravity.TOP
                setPadding(0, dp(10), 0, 0)
            })

            setOnTouchListener { view, event -> handleBubbleTouch(view, event) }
        }

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            dp(56),
            dp(56),
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 400
        }

        layoutParams = params
        windowManager.addView(bubble, params)
        bubbleView = bubble
    }

    private fun handleBubbleTouch(view: View, event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDragging = false
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                initialWindowX = layoutParams!!.x
                initialWindowY = layoutParams!!.y
                true
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX = event.rawX - initialTouchX
                val deltaY = event.rawY - initialTouchY
                if (abs(deltaX) > 10 || abs(deltaY) > 10) {
                    isDragging = true
                }
                layoutParams!!.x = initialWindowX + deltaX.toInt()
                layoutParams!!.y = initialWindowY + deltaY.toInt()
                windowManager.updateViewLayout(bubbleView, layoutParams)
                true
            }
            MotionEvent.ACTION_UP -> {
                if (!isDragging) {
                    togglePanel()
                }
                // 贴边吸附：靠近屏幕边缘时自动贴边
                val screenWidth = resources.displayMetrics.widthPixels
                val bubbleWidth = view.width
                val centerX = layoutParams!!.x + bubbleWidth / 2
                if (centerX < screenWidth / 2) {
                    layoutParams!!.x = 0
                } else {
                    layoutParams!!.x = screenWidth - bubbleWidth
                }
                // 防止滑出屏幕
                val screenHeight = resources.displayMetrics.heightPixels
                if (layoutParams!!.y < 0) layoutParams!!.y = 0
                if (layoutParams!!.y > screenHeight - view.height - dp(100)) {
                    layoutParams!!.y = screenHeight - view.height - dp(100)
                }
                windowManager.updateViewLayout(bubbleView, layoutParams)
                isDragging = false
                true
            }
            else -> false
        }
    }

    private fun togglePanel() {
        if (isPanelShown) {
            dismissPanel()
        } else {
            showPanel()
        }
    }

    private fun showPanel() {
        dismissPanel()
        isPanelShown = true

        // 直接用 LinearLayout 做根视图，避免 FrameLayout 嵌套的测量问题
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(12), dp(16), dp(12))
            minimumWidth = dp(180)

            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dp(14).toFloat()
                setColor(Color.parseColor("#FAFAFA"))
                setStroke(2, Color.parseColor("#E0E0E0"))
            }

            // 标题
            addView(TextView(context).apply {
                text = "NeverForget"
                textSize = 15f
                setTextColor(Color.parseColor("#333333"))
                paint.isFakeBoldText = true
                setPadding(0, 0, 0, dp(10))
            })

            // 数据行
            addView(LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 0, 0, dp(10))

                addView(TextView(context).apply {
                    tag = "msgCount"
                    text = "-- 消息"
                    textSize = 12f
                    setTextColor(Color.GRAY)
                    setPadding(0, 0, dp(16), 0)
                })
                addView(TextView(context).apply {
                    tag = "pendingCount"
                    text = "-- 待摘要"
                    textSize = 12f
                    setTextColor(Color.parseColor("#E65100"))
                })
            })

            // 打开按钮
            addView(createPanelButton("打开 NeverForget", "#4A90D9") {
                startActivity(Intent(this@FloatingWindowService, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
                dismissPanel()
            })

            // 间距
            addView(android.view.View(context).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(6))
            })

            // 扫描屏幕按钮
            addView(createPanelButton("扫描屏幕", "#2E7D32") {
                val ctx = this@FloatingWindowService
                if (!MessageAccessibilityService.isServiceEnabled(ctx)) {
                    android.widget.Toast.makeText(ctx,
                        "请先在设置中开启无障碍服务", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    MessageAccessibilityService.shouldScan = true
                    MessageAccessibilityService.lastScanCount = -1
                    MessageAccessibilityService.lastScanError = null
                    android.widget.Toast.makeText(ctx,
                        "正在扫描屏幕...", android.widget.Toast.LENGTH_SHORT).show()
                    scope.launch {
                        try {
                            for (i in 0 until 30) {
                                kotlinx.coroutines.delay(500)
                                val count = MessageAccessibilityService.lastScanCount
                                val error = MessageAccessibilityService.lastScanError
                                if (count >= 0 || error != null) {
                                    val msg = if (count > 0) "已扫描 $count 条消息，可生成摘要"
                                             else error ?: "扫描完成，未发现消息"
                                    android.widget.Toast.makeText(ctx,
                                        msg, android.widget.Toast.LENGTH_SHORT).show()
                                    // 确保气泡还在，不在就重建
                                    ensureBubbleVisible()
                                    return@launch
                                }
                            }
                            android.widget.Toast.makeText(ctx,
                                "扫描超时，请确认已打开微信聊天界面", android.widget.Toast.LENGTH_SHORT).show()
                            ensureBubbleVisible()
                        } catch (e: Throwable) {
                            android.util.Log.e("NeverForget", "Scan poll error", e)
                            ensureBubbleVisible()
                        }
                    }
                }
            })

            // 间距
            addView(android.view.View(context).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(6))
            })

            // 生成摘要按钮
            addView(createPanelButton("生成 AI 摘要", "#E65100") {
                // 不立即关闭面板，保留气泡可见
                android.widget.Toast.makeText(this@FloatingWindowService,
                    "正在生成摘要...", android.widget.Toast.LENGTH_SHORT).show()
                scope.launch {
                    try {
                        val result = summaryEngine.generateSummary()
                        val msg = if (result.success) "已生成: ${result.summary.title}" else result.error
                        android.widget.Toast.makeText(this@FloatingWindowService,
                            msg, android.widget.Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        android.widget.Toast.makeText(this@FloatingWindowService,
                            "失败: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    ensureBubbleVisible()
                }
            })
        }

        panelView = root

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val panelParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START

            val screenW = resources.displayMetrics.widthPixels
            val screenH = resources.displayMetrics.heightPixels
            val bubbleX = layoutParams?.x ?: 100
            val bubbleY = layoutParams?.y ?: 400
            val panelW = dp(190) // 预估面板宽度

            // 水平方向：如果气泡在右半屏，面板放在左边；否则放右边
            x = if (bubbleX > screenW / 2) {
                maxOf(0, bubbleX - panelW - dp(8))
            } else {
                minOf(screenW - panelW, bubbleX + dp(56) + dp(8))
            }

            // 垂直方向：如果气泡在下半屏，面板放在上方
            y = if (bubbleY > screenH / 2) {
                maxOf(0, bubbleY - dp(180))
            } else {
                minOf(screenH - dp(200), bubbleY + dp(56) + dp(4))
            }
        }

        try {
            windowManager.addView(root, panelParams)
            updateJob = scope.launch { updatePanelData() }
        } catch (e: Exception) {
            isPanelShown = false
        }
    }

    private fun createPanelButton(text: String, color: String, onClick: () -> Unit): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 12f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(dp(14), dp(7), dp(14), dp(7))
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = dp(6).toFloat()
                setColor(Color.parseColor(color))
            }
            setOnClickListener { onClick() }
        }
    }

    private suspend fun updatePanelData() {
        try {
            val messages = messageRepository.getAllMessages().first()
            val unsummarized = messages.count { !it.isSummarized }
            val root = panelView ?: return

            // 遍历子 View 找到数据行
            val statsRow = root.getChildAt(1) as? LinearLayout ?: return
            val msgTv = statsRow.findViewWithTag<TextView>("msgCount") ?: return
            val pendingTv = statsRow.findViewWithTag<TextView>("pendingCount") ?: return

            msgTv.post {
                msgTv.text = "${messages.size} 消息"
                pendingTv.text = "$unsummarized 待摘要"
            }
        } catch (_: Exception) {}
    }

    private fun dismissPanel() {
        isPanelShown = false
        updateJob?.cancel()
        panelView?.let { v ->
            try { windowManager.removeView(v) } catch (_: Exception) {}
        }
        panelView = null
    }

    private fun dismissBubble() {
        bubbleView?.let { v ->
            try { windowManager.removeView(v) } catch (_: Exception) {}
        }
        bubbleView = null
    }

    private fun ensureBubbleVisible() {
        if (bubbleView == null) {
            showBubble()
        }
    }

    private fun createNotification(): Notification {
        val channelId = "floating_window"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "悬浮窗服务",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Builder(this, channelId)
            .setContentTitle("NeverForget")
            .setContentText("悬浮窗运行中")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
