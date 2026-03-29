package com.slots.app.data.repository

import com.slots.app.data.local.dao.TransactionDao
import com.slots.app.data.local.entity.TransactionEntity
import com.slots.app.data.remote.api.SlotsApiService
import com.slots.app.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val apiService: SlotsApiService
) {
    fun getAllTransactions(): Flow<List<Transaction>> =
        transactionDao.getAllTransactions().map { entities -> entities.map { it.toDomain() } }

    fun getTransactionsByType(type: String): Flow<List<Transaction>> =
        transactionDao.getTransactionsByType(type).map { entities -> entities.map { it.toDomain() } }

    fun getTransactionsBetween(start: Long, end: Long): Flow<List<Transaction>> =
        transactionDao.getTransactionsBetween(start, end).map { entities -> entities.map { it.toDomain() } }

    fun getTotalIncome(start: Long, end: Long): Flow<Double?> =
        transactionDao.getTotalIncome(start, end)

    fun getTotalExpenses(start: Long, end: Long): Flow<Double?> =
        transactionDao.getTotalExpenses(start, end)

    suspend fun getTotalExpensesSync(start: Long, end: Long): Double? =
        transactionDao.getTotalExpensesSync(start, end)

    suspend fun insertTransaction(transaction: Transaction): Long =
        transactionDao.insertTransaction(TransactionEntity.fromDomain(transaction))

    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(TransactionEntity.fromDomain(transaction))

    suspend fun deleteTransactionById(id: Long) =
        transactionDao.deleteTransactionById(id)

    suspend fun syncWithRemote(token: String) {
        try {
            val response = apiService.getTransactions("Bearer $token")
            if (response.isSuccessful) {
                response.body()?.transactions?.forEach { apiTx ->
                    val entity = TransactionEntity(
                        type = apiTx.type,
                        amount = apiTx.amount,
                        category = apiTx.category,
                        description = apiTx.description,
                        date = apiTx.date
                    )
                    transactionDao.insertTransaction(entity)
                }
            }
        } catch (e: Exception) {
            // Sync failed silently; local data remains available
        }
    }
}
