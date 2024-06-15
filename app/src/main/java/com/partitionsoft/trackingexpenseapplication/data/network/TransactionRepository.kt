package com.partitionsoft.trackingexpenseapplication.data.network

import androidx.paging.PagingData
import com.partitionsoft.trackingexpenseapplication.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun getAllTransactions(): Flow<PagingData<Transaction>>

    fun getTransactions(): List<Transaction>
    suspend fun addTransaction(transaction: Transaction)
    suspend fun getBitcoinExchangeRate(): Double
}