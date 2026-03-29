package com.slots.app.domain.model

data class Debt(
    val id: Long = 0,
    val personName: String,
    val amount: Double,
    val type: DebtType,
    val description: String = "",
    val status: DebtStatus = DebtStatus.PENDING,
    val date: Long = System.currentTimeMillis()
)

enum class DebtType { BORROWED, LENT }
enum class DebtStatus { PENDING, SETTLED }
