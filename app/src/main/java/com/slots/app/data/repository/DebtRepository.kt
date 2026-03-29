package com.slots.app.data.repository

import com.slots.app.data.local.dao.DebtDao
import com.slots.app.data.local.entity.DebtEntity
import com.slots.app.data.remote.api.SlotsApiService
import com.slots.app.domain.model.Debt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DebtRepository @Inject constructor(
    private val debtDao: DebtDao,
    private val apiService: SlotsApiService
) {
    fun getAllDebts(): Flow<List<Debt>> =
        debtDao.getAllDebts().map { entities -> entities.map { it.toDomain() } }

    fun getDebtsByType(type: String): Flow<List<Debt>> =
        debtDao.getDebtsByType(type).map { entities -> entities.map { it.toDomain() } }

    fun getDebtsByStatus(status: String): Flow<List<Debt>> =
        debtDao.getDebtsByStatus(status).map { entities -> entities.map { it.toDomain() } }

    fun getTotalLent(): Flow<Double?> = debtDao.getTotalLent()

    fun getTotalBorrowed(): Flow<Double?> = debtDao.getTotalBorrowed()

    suspend fun insertDebt(debt: Debt): Long =
        debtDao.insertDebt(DebtEntity.fromDomain(debt))

    suspend fun updateDebt(debt: Debt) =
        debtDao.updateDebt(DebtEntity.fromDomain(debt))

    suspend fun deleteDebt(debt: Debt) =
        debtDao.deleteDebt(DebtEntity.fromDomain(debt))

    suspend fun deleteDebtById(id: Long) =
        debtDao.deleteDebtById(id)

    suspend fun syncWithRemote(token: String) {
        try {
            val response = apiService.getDebts("Bearer $token")
            if (response.isSuccessful) {
                response.body()?.debts?.forEach { apiDebt ->
                    val entity = DebtEntity(
                        personName = apiDebt.personName,
                        amount = apiDebt.amount,
                        type = apiDebt.type,
                        description = apiDebt.description,
                        status = apiDebt.status,
                        date = apiDebt.createdAt
                    )
                    debtDao.insertDebt(entity)
                }
            }
        } catch (e: Exception) {
            // Sync failed silently; local data remains available
        }
    }
}
