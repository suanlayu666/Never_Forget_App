package com.neverforget.util

import java.security.MessageDigest

object HashUtils {

    fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun messageHash(
        sourceApp: String,
        conversationName: String,
        senderName: String,
        content: String,
        timestamp: Long
    ): String {
        val raw = "$sourceApp|$conversationName|$senderName|$content|$timestamp"
        return sha256(raw)
    }
}
