package com.example.algonative.domain.model

import com.example.algonative.data.remote.CompanyProfileDto

data class CompanyProfile(
    val ticker: String,
    val companyName: String,
    val exchange: String,
    val industry: String,
    val logoUrl: String,
    val marketCap: Double,
    val country: String,
    val currency: String,
    val ipoDate: String,
    val website: String?
)

fun CompanyProfileDto.toDomain(): CompanyProfile {
    return CompanyProfile(
        ticker = ticker,
        companyName = name,
        exchange = exchange,
        industry = finnhubIndustry,
        logoUrl = logo,
        marketCap = marketCapitalization,
        country = country,
        currency = currency,
        ipoDate = ipo,
        website = null
    )
}