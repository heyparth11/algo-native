package com.example.algonative.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface AlphaVantageApi {

    @GET("query")
    suspend fun getDailyCandles(
        @Query("function") function: String = "TIME_SERIES_DAILY",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): ResponseBody

    @GET("query")
    suspend fun getWeeklyCandles(
        @Query("function") function: String = "TIME_SERIES_WEEKLY",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): ResponseBody

    @GET("query")
    suspend fun getMonthlyCandles(
        @Query("function") function: String = "TIME_SERIES_MONTHLY",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): ResponseBody
}
