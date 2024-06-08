package com.partitionsoft.trackingexpenseapplication.domain.usecase

import com.partitionsoft.trackingexpenseapplication.data.network.TransactionRepository
import com.partitionsoft.trackingexpenseapplication.domain.model.Transaction

class AddTransactionUseCase(private val repository: TransactionRepository) {
    suspend fun execute(transaction: Transaction) {
        repository.addTransaction(transaction)
    }
}