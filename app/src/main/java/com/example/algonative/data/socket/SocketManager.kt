package com.example.algonative.data.socket

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap

class FinnhubSocketManager(
    private val apiKey: String
) {

    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null

    // Track active observer counts for each symbol
    private val activeSubscriptions = ConcurrentHashMap<String, Int>()

    // Shared flow to broadcast incoming trades to all active collectors
    private val _trades = MutableSharedFlow<Trade>(extraBufferCapacity = 128)
    private val tradesFlow = _trades.asSharedFlow()

    @Synchronized
    private fun connectIfNeeded() {
        if (webSocket != null) return

        Log.d("FinnhubSocketManager", "Connecting WebSocket...")
        val request = Request.Builder()
            .url("wss://ws.finnhub.io?token=$apiKey")
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("FinnhubSocketManager", "WebSocket opened successfully")
                // Re-subscribe to all active symbols on connection establishment
                activeSubscriptions.keys.forEach { symbol ->
                    sendSubscription(webSocket, symbol, "subscribe")
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val json = JSONObject(text)
                    if (!json.has("data")) return

                    val trades = json.getJSONArray("data")
                    for (i in 0 until trades.length()) {
                        val tradeJson = trades.getJSONObject(i)
                        val symbol = tradeJson.getString("s")
                        val price = tradeJson.getDouble("p")
                        _trades.tryEmit(Trade(symbol, price))
                    }
                } catch (e: Exception) {
                    Log.e("FinnhubSocketManager", "Error parsing message: ${e.message}")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("FinnhubSocketManager", "WebSocket failure: ${t.message}", t)
                synchronized(this@FinnhubSocketManager) {
                    this@FinnhubSocketManager.webSocket = null
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("FinnhubSocketManager", "WebSocket closed: $reason")
                synchronized(this@FinnhubSocketManager) {
                    this@FinnhubSocketManager.webSocket = null
                }
            }
        }

        webSocket = client.newWebSocket(request, listener)
    }

    private fun sendSubscription(ws: WebSocket, symbol: String, type: String) {
        val msg = """
            {
                "type": "$type",
                "symbol": "$symbol"
            }
        """.trimIndent()
        ws.send(msg)
    }

    @Synchronized
    fun observeTrades(symbols: List<String>): Flow<Trade> {
        connectIfNeeded()

        // Register interest for symbols
        symbols.forEach { symbol ->
            val count = activeSubscriptions.getOrDefault(symbol, 0)
            activeSubscriptions[symbol] = count + 1
            if (count == 0) {
                webSocket?.let { ws -> sendSubscription(ws, symbol, "subscribe") }
            }
        }

        return flow {
            try {
                tradesFlow.filter { symbols.contains(it.symbol) }.collect {
                    emit(it)
                }
            } finally {
                // Clean up subscriptions when flow collection is cancelled
                synchronized(this@FinnhubSocketManager) {
                    symbols.forEach { symbol ->
                        val count = activeSubscriptions.getOrDefault(symbol, 0)
                        if (count > 1) {
                            activeSubscriptions[symbol] = count - 1
                        } else {
                            activeSubscriptions.remove(symbol)
                            webSocket?.let { ws -> sendSubscription(ws, symbol, "unsubscribe") }
                        }
                    }
                    if (activeSubscriptions.isEmpty()) {
                        Log.d("FinnhubSocketManager", "Closing WebSocket since there are no active subscribers")
                        webSocket?.close(1000, "No active subscribers")
                        webSocket = null
                    }
                }
            }
        }
    }
}
