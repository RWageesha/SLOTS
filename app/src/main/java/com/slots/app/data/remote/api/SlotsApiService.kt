package com.slots.app.data.remote.api

import com.slots.app.data.remote.model.*
import retrofit2.Response
import retrofit2.http.*

interface SlotsApiService {

    // Tasks
    @GET("api/tasks")
    suspend fun getTasks(
        @Header("Authorization") token: String
    ): Response<TaskListResponse>

    @POST("api/tasks")
    suspend fun createTask(
        @Header("Authorization") token: String,
        @Body task: TaskApiModel
    ): Response<ApiResponse<TaskApiModel>>

    @PUT("api/tasks/{id}")
    suspend fun updateTask(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body task: TaskApiModel
    ): Response<ApiResponse<TaskApiModel>>

    @DELETE("api/tasks/{id}")
    suspend fun deleteTask(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>

    // Transactions
    @GET("api/transactions")
    suspend fun getTransactions(
        @Header("Authorization") token: String,
        @Query("month") month: String? = null
    ): Response<TransactionListResponse>

    @POST("api/transactions")
    suspend fun createTransaction(
        @Header("Authorization") token: String,
        @Body transaction: TransactionApiModel
    ): Response<ApiResponse<TransactionApiModel>>

    @DELETE("api/transactions/{id}")
    suspend fun deleteTransaction(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>

    @GET("api/transactions/summary")
    suspend fun getMonthlySummary(
        @Header("Authorization") token: String,
        @Query("month") month: String
    ): Response<MonthlySummary>

    // Debts
    @GET("api/debts")
    suspend fun getDebts(
        @Header("Authorization") token: String
    ): Response<DebtListResponse>

    @POST("api/debts")
    suspend fun createDebt(
        @Header("Authorization") token: String,
        @Body debt: DebtApiModel
    ): Response<ApiResponse<DebtApiModel>>

    @PUT("api/debts/{id}")
    suspend fun updateDebt(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body debt: DebtApiModel
    ): Response<ApiResponse<DebtApiModel>>

    @DELETE("api/debts/{id}")
    suspend fun deleteDebt(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ApiResponse<Unit>>

    // Chatbot
    @POST("api/chatbot")
    suspend fun sendChatMessage(
        @Header("Authorization") token: String,
        @Body request: ChatRequest
    ): Response<ChatResponse>

    // Auth
    @POST("api/auth/verify")
    suspend fun verifyToken(
        @Body request: com.slots.app.data.remote.model.AuthRequest
    ): Response<AuthResponse>
}
