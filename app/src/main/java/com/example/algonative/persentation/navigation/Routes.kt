package com.example.algonative.persentation.navigation

import com.example.algonative.domain.model.StockListItem
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object Routes {
    const val HOME = "home"
    const val SIDEBAR = "sidebar"
    const val SEARCH = "search"
    const val NOTIFICATIONS = "notifications"
    const val ALL_STOCKS = "all-stocks"
    const val ROBO = "robo"

    fun stockDetail(stock: StockListItem): String {
        val json = Json.encodeToString(stock)
        val encodedJson = URLEncoder.encode(json, StandardCharsets.UTF_8.name())
        return "stock-details/$encodedJson"
    }
}