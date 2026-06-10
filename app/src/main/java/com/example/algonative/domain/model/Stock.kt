package com.example.algonative.domain.model

data class Stock(
    val symbol: String,
    val currentPrice: Double,
    val change: Double,
    val changePercent: Double,
    val high: Double,
    val low: Double,
    val open: Double,
    val previousClose: Double,
    val timestamp: Long
)
