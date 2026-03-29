package com.slots.app.data.local.dao

import androidx.room.*
import com.slots.app.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC LIMIT :limit")
    suspend fun getRecentMessages(limit: Int = 20): List<ChatMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()

    @Query("DELETE FROM chat_messages WHERE id NOT IN (SELECT id FROM chat_messages ORDER BY timestamp DESC LIMIT 100)")
    suspend fun pruneOldMessages()
}
