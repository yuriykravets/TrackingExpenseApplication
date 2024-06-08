package com.partitionsoft.trackingexpenseapplication.di

import androidx.room.Room
import com.partitionsoft.trackingexpenseapplication.data.local.AppDatabase
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "transaction_database")
            .build()
    }
    single { get<AppDatabase>().transactionDao() }
}
