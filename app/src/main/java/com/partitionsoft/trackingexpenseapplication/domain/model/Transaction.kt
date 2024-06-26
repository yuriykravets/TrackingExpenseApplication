package com.partitionsoft.trackingexpenseapplication.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey val id: Long,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val timestamp: Long
)

data class TransactionUiModel(
    val amount: Double,
    val category: String
)

enum class TransactionType {
    TOP_UP,
    EXPENSE
}

