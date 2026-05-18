package com.neverforget.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AiResponse(
    val id: String? = null,
    val choices: List<AiChoice>? = null
)

data class AiChoice(
    val index: Int? = null,
    val message: AiResponseMessage? = null
)

data class AiResponseMessage(
    val role: String? = null,
    val content: String? = null
)
