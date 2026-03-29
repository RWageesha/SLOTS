package com.slots.app.di

import android.content.Context
import androidx.room.Room
import com.slots.app.data.local.SlotsDatabase
import com.slots.app.data.local.dao.ChatMessageDao
import com.slots.app.data.local.dao.DebtDao
import com.slots.app.data.local.dao.TaskDao
import com.slots.app.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SlotsDatabase =
        Room.databaseBuilder(context, SlotsDatabase::class.java, SlotsDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTaskDao(db: SlotsDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideTransactionDao(db: SlotsDatabase): TransactionDao = db.transactionDao()

    @Provides
    fun provideDebtDao(db: SlotsDatabase): DebtDao = db.debtDao()

    @Provides
    fun provideChatMessageDao(db: SlotsDatabase): ChatMessageDao = db.chatMessageDao()
}
