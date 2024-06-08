package com.partitionsoft.trackingexpenseapplication.di

import com.partitionsoft.trackingexpenseapplication.domain.usecase.AddTransactionUseCase
import com.partitionsoft.trackingexpenseapplication.domain.usecase.GetBitcoinExchangeRateUseCase
import com.partitionsoft.trackingexpenseapplication.domain.usecase.GetTransactionsUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { AddTransactionUseCase(get()) }
    factory { GetTransactionsUseCase(get()) }
    factory { GetBitcoinExchangeRateUseCase(get()) }
}
