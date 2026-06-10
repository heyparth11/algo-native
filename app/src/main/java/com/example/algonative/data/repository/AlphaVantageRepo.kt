package com.example.algonative.data.repository

import android.util.Log
import com.example.algonative.BuildConfig
import com.example.algonative.data.local.ChartCacheDao
import com.example.algonative.data.local.ChartCacheEntity
import com.example.algonative.data.remote.AlphaVantageApi
import com.example.algonative.data.remote.AlphaVantageProvider
import com.example.algonative.domain.model.Candle
import kotlinx.serialization.json.Json
import org.json.JSONObject
class ChartRepository(
    private val api: AlphaVantageApi,
    private val chartDao: ChartCacheDao
) {

    companion object {

        private const val CACHE_DURATION =
            24 * 60 * 60 * 1000L
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend fun getCandles(
        symbol: String,
        timeframe: String = "1M"
    ): List<Candle> {

        val cacheKey =
            "${symbol}_$timeframe"

        val cached =
            chartDao.getChart(cacheKey)

        val now =
            System.currentTimeMillis()

        if (
            cached != null &&
            now - cached.fetchedAt <
            CACHE_DURATION
        ) {

            Log.d(
                "ChartRepository",
                "Loaded from cache"
            )

            return json.decodeFromString(
                cached.candlesJson
            )
        }

        Log.d(
            "ChartRepository",
            "Loaded from API"
        )

        val candles =
            fetchCandlesFromApi(symbol)

        chartDao.insertChart(
            ChartCacheEntity(
                cacheKey = cacheKey,
                candlesJson =
                    json.encodeToString(
                        candles
                    ),
                fetchedAt = now
            )
        )

        return candles
    }

    private suspend fun fetchCandlesFromApi(
        symbol: String
    ): List<Candle> {

        val response =
            api.getDailyCandles(
                symbol = symbol,
                apiKey =
                    BuildConfig
                        .ALPHA_VANTAGE_API_KEY
            )

        val root =
            JSONObject(
                response.string()
            )

        val timeSeries =
            root.getJSONObject(
                "Time Series (Daily)"
            )

        val candles =
            mutableListOf<Candle>()

        val keys =
            timeSeries.keys()

        while (keys.hasNext()) {

            val date =
                keys.next()

            val item =
                timeSeries.getJSONObject(
                    date
                )

            candles.add(
                Candle(
                    date = date,

                    open =
                        item.getString(
                            "1. open"
                        ).toDouble(),

                    high =
                        item.getString(
                            "2. high"
                        ).toDouble(),

                    low =
                        item.getString(
                            "3. low"
                        ).toDouble(),

                    close =
                        item.getString(
                            "4. close"
                        ).toDouble(),

                    volume =
                        item.getString(
                            "5. volume"
                        ).toLong()
                )
            )
        }

        return candles.sortedBy {
            it.date
        }
    }
}