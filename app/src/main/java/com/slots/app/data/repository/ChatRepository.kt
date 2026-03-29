package com.slots.app.data.repository

import com.slots.app.BuildConfig
import com.slots.app.data.local.dao.ChatMessageDao
import com.slots.app.data.local.entity.ChatMessageEntity
import com.slots.app.data.remote.api.ChatCompletionRequest
import com.slots.app.data.remote.api.OpenAiMessage
import com.slots.app.data.remote.api.OpenAiService
import com.slots.app.domain.model.ChatMessage
import com.slots.app.domain.model.MessageRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
    private val openAiService: OpenAiService
) {
    fun getAllMessages(): Flow<List<ChatMessage>> =
        chatMessageDao.getAllMessages().map { entities -> entities.map { it.toDomain() } }

    suspend fun insertMessage(message: ChatMessage): Long =
        chatMessageDao.insertMessage(ChatMessageEntity.fromDomain(message))

    suspend fun clearHistory() = chatMessageDao.clearAllMessages()

    suspend fun sendMessage(userMessage: String): Result<ChatMessage> {
        return try {
            val recentMessages = chatMessageDao.getRecentMessages(10)

            val systemMessage = OpenAiMessage(
                role = "system",
                content = "You are SLOTS Assistant, a helpful AI for the Smart Life Organizing and Tracking System app. " +
                        "Help users manage tasks, budget, and debts. Be concise and practical."
            )

            val history = recentMessages.map { OpenAiMessage(role = it.role.lowercase(), content = it.content) }
            val newUserMessage = OpenAiMessage(role = "user", content = userMessage)

            val request = ChatCompletionRequest(
                model = "gpt-3.5-turbo",
                messages = listOf(systemMessage) + history + listOf(newUserMessage),
                max_tokens = 500
            )

            val response = openAiService.getChatCompletion(
                // NOTE: In production, remove BuildConfig.OPENAI_API_KEY and instead proxy
                // all AI requests through your backend (backend/api/chatbot.js already does this).
                bearerToken = "Bearer ${BuildConfig.OPENAI_API_KEY}",
                request = request
            )

            val assistantContent = response.choices.firstOrNull()?.message?.content
                ?: "Sorry, I could not generate a response."

            val assistantMessage = ChatMessage(
                content = assistantContent,
                role = MessageRole.ASSISTANT
            )
            chatMessageDao.insertMessage(ChatMessageEntity.fromDomain(assistantMessage))
            chatMessageDao.pruneOldMessages()

            Result.success(assistantMessage)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
