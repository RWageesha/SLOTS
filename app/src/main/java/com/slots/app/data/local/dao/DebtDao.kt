package com.slots.app.data.local.dao

import androidx.room.*
import com.slots.app.data.local.entity.DebtEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {
    @Query("SELECT * FROM debts ORDER BY date DESC")
    fun getAllDebts(): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts WHERE type = :type ORDER BY date DESC")
    fun getDebtsByType(type: String): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts WHERE status = :status ORDER BY date DESC")
    fun getDebtsByStatus(status: String): Flow<List<DebtEntity>>

    @Query("SELECT SUM(amount) FROM debts WHERE type = 'LENT' AND status = 'PENDING'")
    fun getTotalLent(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM debts WHERE type = 'BORROWED' AND status = 'PENDING'")
    fun getTotalBorrowed(): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDebt(debt: DebtEntity): Long

    @Update
    suspend fun updateDebt(debt: DebtEntity)

    @Delete
    suspend fun deleteDebt(debt: DebtEntity)

    @Query("DELETE FROM debts WHERE id = :id")
    suspend fun deleteDebtById(id: Long)
}
