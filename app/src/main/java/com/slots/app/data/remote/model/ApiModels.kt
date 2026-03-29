package com.slots.app.data.remote.model

import com.google.gson.annotations.SerializedName

// Task API models
data class TaskApiModel(
    val id: String? = null,
    val title: String,
    val description: String = "",
    val category: String,
    val priority: String,
    val deadline: Long? = null,
    val status: String = "PENDING",
    @SerializedName("created_at") val createdAt: Long = System.currentTimeMillis()
)

data class TaskListResponse(
    val tasks: List<TaskApiModel>,
    val total: Int
)

// Transaction API models
data class TransactionApiModel(
    val id: String? = null,
    val type: String,
    val amount: Double,
    val category: String,
    val description: String = "",
    val date: Long = System.currentTimeMillis()
)

data class TransactionListResponse(
    val transactions: List<TransactionApiModel>,
    val total: Int
)

data class MonthlySummary(
    val totalIncome: Double,
    val totalExpenses: Double,
    val balance: Double,
    val month: String
)

// Debt API models
data class DebtApiModel(
    val id: String? = null,
    @SerializedName("person_name") val personName: String,
    val amount: Double,
    val type: String,
    val description: String = "",
    val status: String = "PENDING",
    @SerializedName("created_at") val createdAt: Long = System.currentTimeMillis()
)

data class DebtListResponse(
    val debts: List<DebtApiModel>,
    val total: Int
)

// Chatbot API models
data class ChatRequest(
    val message: String,
    @SerializedName("user_id") val userId: String
)

data class ChatResponse(
    val response: String,
    val action: String? = null,
    val actionResult: String? = null
)

// Generic API response
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val error: String? = null
)

// Auth models
data class AuthRequest(
    val token: String
)

data class AuthResponse(
    val userId: String,
    val email: String,
    val name: String
)
