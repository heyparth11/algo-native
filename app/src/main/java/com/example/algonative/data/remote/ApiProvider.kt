package com.example.algonative.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiProvider {

    private const val BASE_URL =
        "https://finnhub.io/api/v1/"

    val api: FinnhubApi by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(FinnhubApi::class.java)
    }
}