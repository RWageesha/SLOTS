package com.slots.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.slots.app.domain.model.Transaction
import com.slots.app.domain.model.TransactionCategory
import com.slots.app.domain.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "date") val date: Long = System.currentTimeMillis()
) {
    fun toDomain(): Transaction = Transaction(
        id = id,
        type = TransactionType.valueOf(type),
        amount = amount,
        category = TransactionCategory.valueOf(category),
        description = description,
        date = date
    )

    companion object {
        fun fromDomain(transaction: Transaction): TransactionEntity = TransactionEntity(
            id = transaction.id,
            type = transaction.type.name,
            amount = transaction.amount,
            category = transaction.category.name,
            description = transaction.description,
            date = transaction.date
        )
    }
}
