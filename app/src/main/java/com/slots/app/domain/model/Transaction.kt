package com.slots.app.domain.model

data class Transaction(
    val id: Long = 0,
    val type: TransactionType,
    val amount: Double,
    val category: TransactionCategory,
    val description: String = "",
    val date: Long = System.currentTimeMillis()
)

enum class TransactionType { INCOME, EXPENSE }
enum class TransactionCategory { FOOD, TRANSPORT, BILLS, EDUCATION, SALARY, OTHER }
