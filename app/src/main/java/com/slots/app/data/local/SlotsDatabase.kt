package com.slots.app.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.slots.app.data.local.dao.ChatMessageDao
import com.slots.app.data.local.dao.DebtDao
import com.slots.app.data.local.dao.TaskDao
import com.slots.app.data.local.dao.TransactionDao
import com.slots.app.data.local.entity.ChatMessageEntity
import com.slots.app.data.local.entity.DebtEntity
import com.slots.app.data.local.entity.TaskEntity
import com.slots.app.data.local.entity.TransactionEntity

@Database(
    entities = [
        TaskEntity::class,
        TransactionEntity::class,
        DebtEntity::class,
        ChatMessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SlotsDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun transactionDao(): TransactionDao
    abstract fun debtDao(): DebtDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        const val DATABASE_NAME = "slots_database"
    }
}
