package com.example.algonative.domain.model

data class BulkInstrument(
    val exchangeSegment: String,
    val exchangeInstrumentID: String,
    val displayName: String = "",
    val lastPrice: String = "",
    val priceChange: String = "",
    val percentChange: String = ""
)