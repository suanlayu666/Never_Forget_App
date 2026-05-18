package com.neverforget

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.neverforget.ui.NeverForgetApp
import com.neverforget.util.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var targetSummaryId: Long? = null

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* granted or not, we try anyway */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Android 13+ 请求通知权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        targetSummaryId = intent?.getLongExtra(NotificationHelper.EXTRA_SUMMARY_ID, -1)
            ?.let { if (it > 0) it else null }

        enableEdgeToEdge()
        setContent {
            NeverForgetApp(
                initialSummaryId = targetSummaryId,
                onNavigated = { targetSummaryId = null }
            )
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        targetSummaryId = intent?.getLongExtra(NotificationHelper.EXTRA_SUMMARY_ID, -1)
            ?.let { if (it > 0) it else null }
        setContent {
            NeverForgetApp(
                initialSummaryId = targetSummaryId,
                onNavigated = { targetSummaryId = null }
            )
        }
    }
}
