package com.example.algonative.data.remote


data class QuoteDto(
    // Current price
    val c: Double,
    // Change
    val d: Double,
    // Change %
    val dp: Double,
    // Day High
    val h: Double,
    // Day Low
    val l: Double,
    // Open Price
    val o: Double,
    // Previous Close
    val pc: Double,
    // Timestamp
    val t: Long
)
