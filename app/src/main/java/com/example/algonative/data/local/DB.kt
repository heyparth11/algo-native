package com.example.algonative.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ChartCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AlgoDatabase : RoomDatabase() {

    abstract fun chartCacheDao():
            ChartCacheDao
}