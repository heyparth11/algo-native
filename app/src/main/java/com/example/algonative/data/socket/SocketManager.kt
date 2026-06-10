package com.example.algonative.data.socket

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

class FinnhubSocketManager(
    private val apiKey: String
) {

    private val client = OkHttpClient()

    fun observeTrades(
        symbols: List<String>
    ): Flow<Trade> = callbackFlow {

        val request = Request.Builder()
            .url("wss://ws.finnhub.io?token=$apiKey")
            .build()

        val listener = object : WebSocketListener() {

            override fun onOpen(
                webSocket: WebSocket,
                response: Response
            ) {

                symbols.forEach { symbol ->

                    val subscribeMessage =
                        """
                        {
                            "type":"subscribe",
                            "symbol":"$symbol"
                        }
                        """.trimIndent()

                    webSocket.send(subscribeMessage)
                }
            }

            override fun onMessage(
                webSocket: WebSocket,
                text: String
            ) {

                try {

                    val json = JSONObject(text)

                    if (!json.has("data")) return

                    val trades =
                        json.getJSONArray("data")

                    for (i in 0 until trades.length()) {

                        val tradeJson =
                            trades.getJSONObject(i)

                        val symbol =
                            tradeJson.getString("s")

                        val price =
                            tradeJson.getDouble("p")

                        trySend(
                            Trade(
                                symbol = symbol,
                                price = price
                            )
                        )
                    }

                } catch (_: Exception) {
                }
            }

            override fun onFailure(
                webSocket: WebSocket,
                t: Throwable,
                response: Response?
            ) {
                android.util.Log.e("FinnhubSocketManager", "WebSocket failure: ${t.message}", t)
                close()
            }
        }

        val socket =
            client.newWebSocket(
                request,
                listener
            )

        awaitClose {
            socket.close(
                1000,
                "Closed by user"
            )
        }
    }
}
