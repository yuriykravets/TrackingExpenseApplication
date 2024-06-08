package com.partitionsoft.trackingexpenseapplication.data.network

import com.partitionsoft.trackingexpenseapplication.domain.model.Transaction

interface TransactionRepository {
    suspend fun getAllTransactions(): List<Transaction>
    suspend fun addTransaction(transaction: Transaction)
    suspend fun getBitcoinExchangeRate(): Double
}