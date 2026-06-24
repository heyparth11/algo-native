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

        val candles = when (timeframe) {
            "1D" -> fetchDailyFromApi(symbol).takeLast(10) // Show last 10 days of daily data
            "1W" -> fetchDailyFromApi(symbol).takeLast(5)  // Show last 5 trading days
            "1M" -> fetchDailyFromApi(symbol).takeLast(22) // Show last 22 trading days
            "1Y" -> fetchWeeklyFromApi(symbol).takeLast(52) // Show last 52 weekly candles (1 year)
            "3Y" -> fetchMonthlyFromApi(symbol).takeLast(36) // Show last 36 monthly candles (3 years)
            else -> fetchMonthlyFromApi(symbol) // "ALL" - Show all monthly candles
        }

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

    private suspend fun fetchDailyFromApi(symbol: String): List<Candle> {
        val response = api.getDailyCandles(
            symbol = symbol,
            apiKey = BuildConfig.ALPHA_VANTAGE_API_KEY
        )
        return parseCandles(response.string(), "Time Series (Daily)")
    }

    private suspend fun fetchWeeklyFromApi(symbol: String): List<Candle> {
        val response = api.getWeeklyCandles(
            symbol = symbol,
            apiKey = BuildConfig.ALPHA_VANTAGE_API_KEY
        )
        return parseCandles(response.string(), "Weekly Time Series")
    }

    private suspend fun fetchMonthlyFromApi(symbol: String): List<Candle> {
        val response = api.getMonthlyCandles(
            symbol = symbol,
            apiKey = BuildConfig.ALPHA_VANTAGE_API_KEY
        )
        return parseCandles(response.string(), "Monthly Time Series")
    }

    private fun parseCandles(jsonString: String, timeSeriesKey: String): List<Candle> {
        val root = JSONObject(jsonString)
        
        // Handle potential error responses from API or API limit notes
        if (root.has("Note")) {
            val note = root.getString("Note")
            Log.w("ChartRepository", "AlphaVantage API Note: $note")
            throw Exception(note)
        }
        if (root.has("Error Message")) {
            val errMsg = root.getString("Error Message")
            Log.w("ChartRepository", "AlphaVantage API Error: $errMsg")
            throw Exception(errMsg)
        }

        val timeSeries = root.getJSONObject(timeSeriesKey)
        val candles = mutableListOf<Candle>()
        val keys = timeSeries.keys()

        while (keys.hasNext()) {
            val date = keys.next()
            val item = timeSeries.getJSONObject(date)
            candles.add(
                Candle(
                    date = date,
                    open = item.getString("1. open").toDouble(),
                    high = item.getString("2. high").toDouble(),
                    low = item.getString("3. low").toDouble(),
                    close = item.getString("4. close").toDouble(),
                    volume = item.getString("5. volume").toLong()
                )
            )
        }

        return candles.sortedBy { it.date }
    }
}