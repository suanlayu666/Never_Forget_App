package com.neverforget.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neverforget.ui.main.MessageListScreen
import com.neverforget.ui.summary.SummaryDetailScreen
import com.neverforget.ui.summary.SummaryListScreen
import com.neverforget.ui.settings.SettingsScreen

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

object Routes {
    const val MESSAGES = "messages"
    const val SUMMARIES = "summaries"
    const val SUMMARY_DETAIL = "summary_detail/{summaryId}"
    const val SETTINGS = "settings"

    fun summaryDetail(id: Long) = "summary_detail/$id"
}

private val bottomNavItems = listOf(
    BottomNavItem(Routes.MESSAGES, "消息", Icons.Filled.ChatBubbleOutline, Icons.Outlined.ChatBubbleOutline),
    BottomNavItem(Routes.SUMMARIES, "摘要", Icons.Filled.Summarize, Icons.Outlined.Summarize),
    BottomNavItem(Routes.SETTINGS, "设置", Icons.Filled.Settings, Icons.Outlined.Settings),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeverForgetApp(
    initialSummaryId: Long? = null,
    onNavigated: (() -> Unit)? = null
) {
    val navController = rememberNavController()

    // 处理通知跳转
    LaunchedEffect(initialSummaryId) {
        initialSummaryId?.let { id ->
            navController.navigate(Routes.summaryDetail(id))
            onNavigated?.invoke()
        }
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 只在主页面显示 BottomNavBar
    val showBottomBar = currentRoute in listOf(Routes.MESSAGES, Routes.SUMMARIES, Routes.SETTINGS)

    Scaffold(
        topBar = {
            val title = when (currentRoute) {
                Routes.MESSAGES -> "消息列表"
                Routes.SUMMARIES -> "摘要列表"
                Routes.SETTINGS -> "设置"
                else -> "NeverForget"
            }
            val showTopBar = showBottomBar // 主页面显示顶栏
            if (showTopBar) {
                TopAppBar(
                    title = { Text(title) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (currentRoute == item.route)
                                        item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.MESSAGES,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.MESSAGES) {
                MessageListScreen()
            }
            composable(Routes.SUMMARIES) {
                SummaryListScreen(
                    onSummaryClick = { summaryId ->
                        navController.navigate(Routes.summaryDetail(summaryId))
                    }
                )
            }
            composable(
                route = Routes.SUMMARY_DETAIL,
                arguments = listOf(navArgument("summaryId") { type = NavType.LongType })
            ) { backStackEntry ->
                val summaryId = backStackEntry.arguments?.getLong("summaryId") ?: return@composable
                SummaryDetailScreen(
                    summaryId = summaryId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen()
            }
        }
    }
}
