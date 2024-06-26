package com.partitionsoft.trackingexpenseapplication.data.network

import com.partitionsoft.trackingexpenseapplication.data.local.TransactionDao
import com.partitionsoft.trackingexpenseapplication.domain.model.Transaction

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val apiService: TransactionApiService
) : TransactionRepository {
    override suspend fun getAllTransactions(page: Int, pageSize: Int): List<Transaction> {
        val offset = (page - 1) * pageSize
        return transactionDao.getAllTransactionWithPagination(offset, pageSize)
    }

    override suspend fun addTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    override suspend fun getBitcoinExchangeRate(): Double {
        val response = apiService.getBitcoinPrice()
        return response.bpi.USD.rate_float
    }
}