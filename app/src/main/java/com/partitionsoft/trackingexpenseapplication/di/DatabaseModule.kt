package com.partitionsoft.trackingexpenseapplication.di

import androidx.room.Room
import com.partitionsoft.trackingexpenseapplication.data.local.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { AppDatabase.getInstance(androidContext()).transactionDao() }
}
