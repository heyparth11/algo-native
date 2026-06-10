package com.example.algonative.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chart_cache")
data class ChartCacheEntity(

    @PrimaryKey
    val cacheKey: String,

    val candlesJson: String,

    val fetchedAt: Long
)