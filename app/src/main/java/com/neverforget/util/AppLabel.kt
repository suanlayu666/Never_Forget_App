package com.neverforget.util

object AppLabel {
    fun displayName(sourceApp: String): String = when (sourceApp) {
        "wechat" -> "微信"
        "wework" -> "企业微信"
        "qq" -> "QQ"
        else -> sourceApp.ifEmpty { "未知" }
    }
}
