package com.slots.app.domain.model

data class ChatMessage(
    val id: Long = 0,
    val content: String,
    val role: MessageRole,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MessageRole { USER, ASSISTANT }
