package com.slots.app.domain.usecase

import com.slots.app.data.repository.TransactionRepository
import com.slots.app.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject

class GetAllTransactionsUseCase @Inject constructor(private val repository: TransactionRepository) {
    operator fun invoke(): Flow<List<Transaction>> = repository.getAllTransactions()
}

class GetTransactionsByTypeUseCase @Inject constructor(private val repository: TransactionRepository) {
    operator fun invoke(type: String): Flow<List<Transaction>> = repository.getTransactionsByType(type)
}

class GetMonthlyTransactionsUseCase @Inject constructor(private val repository: TransactionRepository) {
    operator fun invoke(): Flow<List<Transaction>> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        val start = calendar.timeInMillis
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val end = calendar.timeInMillis
        return repository.getTransactionsBetween(start, end)
    }
}

class GetMonthlyIncomeUseCase @Inject constructor(private val repository: TransactionRepository) {
    operator fun invoke(): Flow<Double?> {
        val (start, end) = getMonthRange()
        return repository.getTotalIncome(start, end)
    }
}

class GetMonthlyExpensesUseCase @Inject constructor(private val repository: TransactionRepository) {
    operator fun invoke(): Flow<Double?> {
        val (start, end) = getMonthRange()
        return repository.getTotalExpenses(start, end)
    }
}

class InsertTransactionUseCase @Inject constructor(private val repository: TransactionRepository) {
    suspend operator fun invoke(transaction: Transaction): Long = repository.insertTransaction(transaction)
}

class DeleteTransactionUseCase @Inject constructor(private val repository: TransactionRepository) {
    suspend operator fun invoke(transaction: Transaction) = repository.deleteTransaction(transaction)
}

private fun getMonthRange(): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    val start = calendar.timeInMillis
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    val end = calendar.timeInMillis
    return Pair(start, end)
}

data class TransactionUseCases(
    val getAllTransactions: GetAllTransactionsUseCase,
    val getTransactionsByType: GetTransactionsByTypeUseCase,
    val getMonthlyTransactions: GetMonthlyTransactionsUseCase,
    val getMonthlyIncome: GetMonthlyIncomeUseCase,
    val getMonthlyExpenses: GetMonthlyExpensesUseCase,
    val insertTransaction: InsertTransactionUseCase,
    val deleteTransaction: DeleteTransactionUseCase
)
