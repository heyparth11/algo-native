package com.example.algonative.di

import com.example.algonative.data.remote.AlphaVantageApi
import com.example.algonative.data.remote.AlphaVantageProvider
import com.example.algonative.domain.InstrumentApi
import com.example.algonative.domain.SubscriptionApi
import com.example.algonative.domain.model.BulkInstrument
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAlphaVantageApi(): AlphaVantageApi {
        return AlphaVantageProvider.api
    }

    @Provides
    @Singleton
    fun provideInstrumentApi(): InstrumentApi {
        return object : InstrumentApi {
            override suspend fun getCategorizedEQ(tab: String): List<BulkInstrument> {
                return listOf(
                    BulkInstrument("NSE", "1", "Mock Stock 1", "100.0", "1.0", "1.0%"),
                    BulkInstrument("NSE", "2", "Mock Stock 2", "200.0", "2.0", "1.0%")
                )
            }
        }
    }

    @Provides
    @Singleton
    fun provideSubscriptionApi(): SubscriptionApi {
        return object : SubscriptionApi {
            override suspend fun subscribeBulkInstrument(instruments: List<Map<String, Any>>) {}
            override suspend fun unsubscribeBulkInstrument(instruments: List<Map<String, Any>>) {}
        }
    }
}