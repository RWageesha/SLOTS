package com.slots.app.domain.usecase

import com.slots.app.data.repository.ChatRepository
import com.slots.app.domain.model.ChatMessage
import com.slots.app.domain.model.MessageRole
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatHistoryUseCase @Inject constructor(private val repository: ChatRepository) {
    operator fun invoke(): Flow<List<ChatMessage>> = repository.getAllMessages()
}

class SendChatMessageUseCase @Inject constructor(private val repository: ChatRepository) {
    suspend operator fun invoke(userMessage: String): Result<ChatMessage> {
        val userMsg = ChatMessage(content = userMessage, role = MessageRole.USER)
        repository.insertMessage(userMsg)
        return repository.sendMessage(userMessage)
    }
}

class ClearChatHistoryUseCase @Inject constructor(private val repository: ChatRepository) {
    suspend operator fun invoke() = repository.clearHistory()
}

data class ChatUseCases(
    val getChatHistory: GetChatHistoryUseCase,
    val sendChatMessage: SendChatMessageUseCase,
    val clearChatHistory: ClearChatHistoryUseCase
)
