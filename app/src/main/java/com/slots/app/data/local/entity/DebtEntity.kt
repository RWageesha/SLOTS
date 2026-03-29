package com.slots.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slots.app.domain.model.Debt
import com.slots.app.domain.model.DebtStatus
import com.slots.app.domain.model.DebtType

@Entity(tableName = "debts")
data class DebtEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "person_name") val personName: String,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "date") val date: Long = System.currentTimeMillis()
) {
    fun toDomain(): Debt = Debt(
        id = id,
        personName = personName,
        amount = amount,
        type = DebtType.valueOf(type),
        description = description,
        status = DebtStatus.valueOf(status),
        date = date
    )

    companion object {
        fun fromDomain(debt: Debt): DebtEntity = DebtEntity(
            id = debt.id,
            personName = debt.personName,
            amount = debt.amount,
            type = debt.type.name,
            description = debt.description,
            status = debt.status.name,
            date = debt.date
        )
    }
}
