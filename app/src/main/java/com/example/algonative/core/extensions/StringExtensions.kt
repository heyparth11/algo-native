package com.example.algonative.core.extensions

fun String.shortExchangeName(): String {

    val exchange = uppercase()

    return when {
        exchange.contains("NASDAQ") -> "NASDAQ"
        exchange.contains("NYSE") -> "NYSE"
        exchange.contains("ARCA") -> "NYSE ARCA"
        exchange.contains("AMEX") -> "AMEX"
        exchange.contains("LONDON") -> "LSE"
        exchange.contains("TOKYO") -> "TSE"
        else -> substringBefore("-").trim()
    }
}