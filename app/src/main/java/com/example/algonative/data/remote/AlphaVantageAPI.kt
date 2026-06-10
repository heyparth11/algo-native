package com.example.algonative.data.remote

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

interface AlphaVantageApi {

    @GET("query")
    suspend fun getDailyCandles(
        @Query("function")
        function: String = "TIME_SERIES_DAILY",

        @Query("symbol")
        symbol: String,

        @Query("apikey")
        apiKey: String
    ): ResponseBody
}