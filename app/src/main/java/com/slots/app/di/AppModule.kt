package com.slots.app.di

import com.slots.app.data.local.dao.ChatMessageDao
import com.slots.app.data.local.dao.DebtDao
import com.slots.app.data.local.dao.TaskDao
import com.slots.app.data.local.dao.TransactionDao
import com.slots.app.data.remote.api.OpenAiService
import com.slots.app.data.remote.api.SlotsApiService
import com.slots.app.data.repository.ChatRepository
import com.slots.app.data.repository.DebtRepository
import com.slots.app.data.repository.TaskRepository
import com.slots.app.data.repository.TransactionRepository
import com.slots.app.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: TaskDao, apiService: SlotsApiService): TaskRepository =
        TaskRepository(taskDao, apiService)

    @Provides
    @Singleton
    fun provideTransactionRepository(
        transactionDao: TransactionDao,
        apiService: SlotsApiService
    ): TransactionRepository = TransactionRepository(transactionDao, apiService)

    @Provides
    @Singleton
    fun provideDebtRepository(debtDao: DebtDao, apiService: SlotsApiService): DebtRepository =
        DebtRepository(debtDao, apiService)

    @Provides
    @Singleton
    fun provideChatRepository(chatMessageDao: ChatMessageDao, openAiService: OpenAiService): ChatRepository =
        ChatRepository(chatMessageDao, openAiService)

    @Provides
    @Singleton
    fun provideTaskUseCases(repository: TaskRepository): TaskUseCases = TaskUseCases(
        getAllTasks = GetAllTasksUseCase(repository),
        getPendingTasks = GetPendingTasksUseCase(repository),
        getTasksByCategory = GetTasksByCategoryUseCase(repository),
        insertTask = InsertTaskUseCase(repository),
        updateTask = UpdateTaskUseCase(repository),
        deleteTask = DeleteTaskUseCase(repository),
        toggleTaskStatus = ToggleTaskStatusUseCase(repository)
    )

    @Provides
    @Singleton
    fun provideTransactionUseCases(repository: TransactionRepository): TransactionUseCases = TransactionUseCases(
        getAllTransactions = GetAllTransactionsUseCase(repository),
        getTransactionsByType = GetTransactionsByTypeUseCase(repository),
        getMonthlyTransactions = GetMonthlyTransactionsUseCase(repository),
        getMonthlyIncome = GetMonthlyIncomeUseCase(repository),
        getMonthlyExpenses = GetMonthlyExpensesUseCase(repository),
        insertTransaction = InsertTransactionUseCase(repository),
        deleteTransaction = DeleteTransactionUseCase(repository)
    )

    @Provides
    @Singleton
    fun provideChatUseCases(repository: ChatRepository): ChatUseCases = ChatUseCases(
        getChatHistory = GetChatHistoryUseCase(repository),
        sendChatMessage = SendChatMessageUseCase(repository),
        clearChatHistory = ClearChatHistoryUseCase(repository)
    )
}
