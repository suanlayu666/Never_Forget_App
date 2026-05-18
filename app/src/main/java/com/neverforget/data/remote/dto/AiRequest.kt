package com.neverforget.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AiRequest(
    val model: String,
    val messages: List<AiMessage>,
    val temperature: Float = 0.7f,
    @SerializedName("response_format")
    val responseFormat: AiResponseFormat? = null
)

data class AiMessage(
    val role: String, // "system" | "user"
    val content: String
)

data class AiResponseFormat(
    val type: String = "json_object"
)
