package com.example.algonative.domain

interface SubscriptionApi {
    suspend fun subscribeBulkInstrument(instruments: List<Map<String, Any>>)
    suspend fun unsubscribeBulkInstrument(instruments: List<Map<String, Any>>)
}