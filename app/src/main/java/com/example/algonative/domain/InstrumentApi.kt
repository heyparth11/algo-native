package com.example.algonative.domain

import com.example.algonative.domain.model.BulkInstrument

interface InstrumentApi {
    suspend fun getCategorizedEQ(tab: String): List<BulkInstrument>
}