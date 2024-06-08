package com.partitionsoft.trackingexpenseapplication.di

import com.partitionsoft.trackingexpenseapplication.data.network.TransactionRepository
import com.partitionsoft.trackingexpenseapplication.data.network.TransactionRepositoryImpl
import org.koin.dsl.module

val repositoryModule = module {
    single<TransactionRepository> { TransactionRepositoryImpl(get(), get()) }
}
