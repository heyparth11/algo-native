package com.example.algonative.data.repository

import com.example.algonative.BuildConfig
import com.example.algonative.core.extensions.shortExchangeName
import com.example.algonative.data.remote.FinnhubApi
import com.example.algonative.domain.model.CompanyProfile
import com.example.algonative.domain.model.Stock
import com.example.algonative.domain.model.StockListItem
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class StockRepository @Inject constructor(
    private val api: FinnhubApi
) {

    private val symbols = listOf(
        "AAPL",
        "MSFT",
        "GOOGL",
        "AMZN",
        "TSLA"
    )

    suspend fun getStocks(): List<Stock> {

        return symbols.map { symbol ->

            val quote = api.getQuote(
                symbol = symbol,
                token = BuildConfig.FINNHUB_API_KEY
            )

            Stock(
                symbol = symbol,
                currentPrice = quote.c,
                change = quote.d,
                changePercent = quote.dp,
                high = quote.h,
                low = quote.l,
                open = quote.o,
                previousClose = quote.pc,
                timestamp = quote.t
            )
        }
    }

    suspend fun getStock(symbol: String): Stock {

        val quote = api.getQuote(
            symbol = symbol,
            token = BuildConfig.FINNHUB_API_KEY
        )

        return Stock(
            symbol = symbol,
            currentPrice = quote.c,
            change = quote.d,
            changePercent = quote.dp,
            high = quote.h,
            low = quote.l,
            open = quote.o,
            previousClose = quote.pc,
            timestamp = quote.t
        )
    }

    suspend fun getSingleStockItem(symbol: String): StockListItem {

        val stock = getStock(symbol)

        val profile =
            getCompanyProfile(
                stock.symbol
            )

        return StockListItem(
            symbol = stock.symbol,
            currentPrice = stock.currentPrice,
            changePercent = stock.changePercent,
            previousClose = stock.previousClose,
            high = stock.high,
            low = stock.low,
            change = stock.change,
            companyName = profile.companyName,
            exchange = profile.exchange.shortExchangeName(),
            logoUrl = profile.logoUrl
        )
    }

    suspend fun getProfiles(): List<CompanyProfile> {
        return symbols.map { symbol ->
            val data = api
                .getCompanyProfile(
                    symbol = symbol,
                    token = BuildConfig.FINNHUB_API_KEY
                )

            CompanyProfile(
                ticker = data.ticker,
                companyName = data.name,
                exchange = data.exchange,
                industry = data.finnhubIndustry,
                logoUrl = data.logo,
                marketCap = data.marketCapitalization,
                country = data.country,
                currency = data.currency,
                ipoDate = data.ipo,
                website = null
            )
        }
    }

    suspend fun getCompanyProfile(
        symbol: String
    ): CompanyProfile {

        val data = api
            .getCompanyProfile(
                symbol = symbol,
                token = BuildConfig.FINNHUB_API_KEY
            )

        return CompanyProfile(
            ticker = data.ticker,
            companyName = data.name,
            exchange = data.exchange,
            industry = data.finnhubIndustry,
            logoUrl = data.logo,
            marketCap = data.marketCapitalization,
            country = data.country,
            currency = data.currency,
            ipoDate = data.ipo,
            website = null
        )
    }

    suspend fun getStockListItems(): List<StockListItem> =
        coroutineScope {

            val stocks = getStocks()

            stocks.map { stock ->

                async {

                    val profile =
                        getCompanyProfile(
                            stock.symbol
                        )

                    StockListItem(
                        symbol = stock.symbol,
                        currentPrice = stock.currentPrice,
                        changePercent = stock.changePercent,
                        previousClose = stock.previousClose,
                        high = stock.high,
                        low = stock.low,
                        change = stock.change,
                        companyName = profile.companyName,
                        exchange = profile.exchange.shortExchangeName(),
                        logoUrl = profile.logoUrl
                    )
                }
            }.awaitAll()
        }
}
