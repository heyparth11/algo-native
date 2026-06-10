package com.example.algonative.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AlphaVantageProvider {

    val api: AlphaVantageApi by lazy {

        Retrofit.Builder()
            .baseUrl(
                "https://www.alphavantage.co/"
            )
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(
                AlphaVantageApi::class.java
            )
    }
}