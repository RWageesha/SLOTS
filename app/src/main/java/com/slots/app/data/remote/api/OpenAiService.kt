package com.slots.app.data.remote.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class OpenAiMessage(
    val role: String,
    val content: String
)

data class ChatCompletionRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<OpenAiMessage>,
    val max_tokens: Int = 500
)

data class Choice(
    val message: OpenAiMessage,
    val finish_reason: String? = null
)

data class ChatCompletionResponse(
    val choices: List<Choice>
)

interface OpenAiService {
    @POST("chat/completions")
    suspend fun getChatCompletion(
        @Header("Authorization") bearerToken: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse
}
