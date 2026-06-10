package com.example.algonative.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class StockListItem(
    val symbol: String,
    val currentPrice: Double,
    val previousClose: Double,
    val high: Double,
    val low: Double,
    val change: Double,
    val changePercent: Double,

    val companyName: String,
    val exchange: String,
    val logoUrl: String
)
