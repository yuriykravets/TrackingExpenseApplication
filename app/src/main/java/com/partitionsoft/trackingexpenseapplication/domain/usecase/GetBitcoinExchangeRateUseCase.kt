package com.partitionsoft.trackingexpenseapplication.domain.usecase

import com.partitionsoft.trackingexpenseapplication.data.network.TransactionRepository

class GetBitcoinExchangeRateUseCase(private val repository: TransactionRepository) {
    suspend fun execute(): Double {
        return repository.getBitcoinExchangeRate()
    }
}