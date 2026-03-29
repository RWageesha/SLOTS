package com.slots.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slots.app.domain.model.ChatMessage
import com.slots.app.domain.model.MessageRole

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "role") val role: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
) {
    fun toDomain(): ChatMessage = ChatMessage(
        id = id,
        content = content,
        role = MessageRole.valueOf(role),
        timestamp = timestamp
    )

    companion object {
        fun fromDomain(message: ChatMessage): ChatMessageEntity = ChatMessageEntity(
            id = message.id,
            content = message.content,
            role = message.role.name,
            timestamp = message.timestamp
        )
    }
}
