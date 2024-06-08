package com.partitionsoft.trackingexpenseapplication

import android.app.Application
import com.partitionsoft.trackingexpenseapplication.di.databaseModule
import com.partitionsoft.trackingexpenseapplication.di.networkModule
import com.partitionsoft.trackingexpenseapplication.di.repositoryModule
import com.partitionsoft.trackingexpenseapplication.di.useCaseModule
import com.partitionsoft.trackingexpenseapplication.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TrackingExpenseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TrackingExpenseApplication)
            modules(
                listOf(
                    networkModule,
                    databaseModule,
                    repositoryModule,
                    useCaseModule,
                    viewModelModule
                )
            )
        }
    }
}