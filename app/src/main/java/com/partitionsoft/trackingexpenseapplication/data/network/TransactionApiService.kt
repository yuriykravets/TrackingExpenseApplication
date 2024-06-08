package com.partitionsoft.trackingexpenseapplication.data.network

import com.partitionsoft.trackingexpenseapplication.domain.model.BitcoinPriceResponse
import retrofit2.http.GET

interface TransactionApiService {
    @GET("bpi/currentprice.json")
    suspend fun getBitcoinPrice(): BitcoinPriceResponse
}