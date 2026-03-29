package com.slots.app.data.local.dao

import androidx.room.*
import com.slots.app.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getTransactionsBetween(start: Long, end: Long): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME' AND date BETWEEN :start AND :end")
    fun getTotalIncome(start: Long, end: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :start AND :end")
    fun getTotalExpenses(start: Long, end: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :start AND :end")
    suspend fun getTotalExpensesSync(start: Long, end: Long): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)
}
