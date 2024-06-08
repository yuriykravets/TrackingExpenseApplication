package com.partitionsoft.trackingexpenseapplication.domain.model

data class BitcoinPriceResponse(
    val time: Time,
    val disclaimer: String,
    val chartName: String,
    val bpi: Bpi
)