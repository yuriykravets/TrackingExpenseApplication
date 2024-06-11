package com.partitionsoft.trackingexpenseapplication.domain.usecase

import com.partitionsoft.trackingexpenseapplication.data.network.TransactionRepository
import com.partitionsoft.trackingexpenseapplication.domain.model.Transaction

class GetTransactionsUseCase(private val repository: TransactionRepository) {
    suspend fun execute(page: Int, pageSize: Int): List<Transaction> {
        return repository.getAllTransactions(page, pageSize)
    }

}