package com.example.algonative.persentation.stockDetails

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algonative.BuildConfig
import com.example.algonative.data.repository.ChartRepository
import com.example.algonative.data.repository.StockRepository
import com.example.algonative.data.socket.FinnhubSocketManager
import com.example.algonative.domain.model.Candle
import com.example.algonative.domain.model.StockListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class StockDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val chartRepository: ChartRepository
) : ViewModel() {

    private val repository = StockRepository()

    private val socketManager =
        FinnhubSocketManager(
            BuildConfig.FINNHUB_API_KEY
        )

    private val _uiState =
        MutableStateFlow<StockDetailUiState>(
            StockDetailUiState.Loading
        )

    private val _stock =
        MutableStateFlow<StockListItem?>(null)

    val stock = _stock.asStateFlow()

    private val _candles =
        MutableStateFlow<List<Candle>>(emptyList())

    val candles = _candles.asStateFlow()

    val uiState =
        _uiState.asStateFlow()

    private val stockListItem: StockListItem? by lazy {
        savedStateHandle.get<String>("stockJson")?.let { encodedJson ->
            try {
                val decodedJson = URLDecoder.decode(encodedJson, StandardCharsets.UTF_8.name())
                Json.decodeFromString<StockListItem>(decodedJson)
            } catch (e: Exception) {
                Log.e("StockDetailVM", "Failed to parse stockJson", e)
                null
            }
        }
    }

    private val symbol: String
        get() = stockListItem?.symbol.orEmpty()

    init {
        loadStock()
    }

    private fun loadStock() {

        val initialStock = stockListItem

        if (initialStock == null) {

            _uiState.value =
                StockDetailUiState.Error(
                    "Stock information not found"
                )

            return
        }

        _stock.value = initialStock

        _uiState.value =
            StockDetailUiState.Success

        observeSocket()
        loadCandles()
    }

    private fun loadCandles() {

        viewModelScope.launch {

            try {

                val candles =
                    chartRepository.getCandles(
                        symbol
                    )

                _candles.value = candles

            } catch (e: Exception) {

                Log.e(
                    "StockDetail",
                    "Failed to load candles",
                    e
                )
            }
        }
    }

    private fun observeSocket() {
        if (symbol.isEmpty()) return
        viewModelScope.launch {
            try {
                socketManager
                    .observeTrades(listOf(symbol))
                    .collect { trade ->
                        updatePrice(
                            trade.price
                        )
                    }
            } catch (e: Exception) {
                Log.e("StockDetailViewModel", "Socket connection error", e)
            }
        }
    }

    private fun updatePrice(
        price: Double
    ) {

        val stock =
            _stock.value ?: return

        val change =
            price - stock.previousClose

        val percent =
            if (stock.previousClose != 0.0) {

                (change / stock.previousClose) * 100

            } else {

                0.0
            }

        _stock.value =
            stock.copy(
                currentPrice = price,
                high = maxOf(
                    stock.high,
                    price
                ),
                low = minOf(
                    stock.low,
                    price
                ),
                change = change,
                changePercent = percent
            )
    }
}

sealed interface StockDetailUiState {
    data object Loading : StockDetailUiState
    data object Success : StockDetailUiState
    data class Error(val message: String) : StockDetailUiState
}