package com.example.algonative.data.repository

import com.example.algonative.data.local.ChartCacheDao
import com.example.algonative.data.remote.AlphaVantageApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideChartRepository(
        api: AlphaVantageApi,
        chartDao: ChartCacheDao
    ): ChartRepository {

        return ChartRepository(
            api,
            chartDao
        )
    }
}