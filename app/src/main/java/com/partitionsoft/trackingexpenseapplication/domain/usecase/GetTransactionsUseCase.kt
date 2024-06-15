package com.partitionsoft.trackingexpenseapplication.domain.usecase

import androidx.paging.PagingData
import com.partitionsoft.trackingexpenseapplication.data.network.TransactionRepository
import com.partitionsoft.trackingexpenseapplication.domain.model.Transaction
import kotlinx.coroutines.flow.Flow

class GetTransactionsUseCase(private val repository: TransactionRepository) {
    fun execute(): Flow<PagingData<Transaction>> {
        return repository.getAllTransactions()
    }
}
