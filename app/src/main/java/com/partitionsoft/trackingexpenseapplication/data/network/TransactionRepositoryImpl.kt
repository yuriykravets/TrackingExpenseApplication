package com.partitionsoft.trackingexpenseapplication.data.network

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.partitionsoft.trackingexpenseapplication.data.local.TransactionDao
import com.partitionsoft.trackingexpenseapplication.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao,
    private val apiService: TransactionApiService
) : TransactionRepository {

    override fun getTransactions(): List<Transaction> {
        return transactionDao.getTransactions()
    }
    override fun getAllTransactions(): Flow<PagingData<Transaction>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { transactionDao.getAllTransactions() }
        ).flow
    }

    override suspend fun addTransaction(transaction: Transaction) {
        transactionDao.insertTransaction(transaction)
    }

    override suspend fun getBitcoinExchangeRate(): Double {
        val response = apiService.getBitcoinPrice()
        return response.bpi.USD.rate_float
    }
}