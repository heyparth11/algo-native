package com.example.algonative.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChartCacheDao {

    @Query(
        """
        SELECT *
        FROM chart_cache
        WHERE cacheKey = :cacheKey
        """
    )
    suspend fun getChart(
        cacheKey: String
    ): ChartCacheEntity?

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertChart(
        chart: ChartCacheEntity
    )

    @Query(
        """
        DELETE FROM chart_cache
        WHERE cacheKey = :cacheKey
        """
    )
    suspend fun deleteChart(
        cacheKey: String
    )
}