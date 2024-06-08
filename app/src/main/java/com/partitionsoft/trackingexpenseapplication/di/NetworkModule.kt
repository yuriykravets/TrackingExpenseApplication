package com.partitionsoft.trackingexpenseapplication.di

import com.partitionsoft.trackingexpenseapplication.common.BASE_URL
import com.partitionsoft.trackingexpenseapplication.data.network.TransactionApiService
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TransactionApiService::class.java)
    }
}
