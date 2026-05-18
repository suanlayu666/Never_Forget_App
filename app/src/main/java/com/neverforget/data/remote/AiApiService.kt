package com.neverforget.data.remote

import com.neverforget.data.remote.dto.AiRequest
import com.neverforget.data.remote.dto.AiResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AiApiService {

    @POST("chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") authorization: String,
        @Body request: AiRequest
    ): AiResponse
}
