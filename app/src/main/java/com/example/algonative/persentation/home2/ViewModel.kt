package com.example.algonative.persentation.home2

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algonative.BuildConfig
import com.example.algonative.data.repository.StockRepository
import com.example.algonative.data.socket.FinnhubSocketManager
import com.example.algonative.domain.model.StockListItem
import com.example.algonative.persentation.navigation.NavigationManager
import com.example.algonative.persentation.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockViewModel @Inject constructor(
    private val navigationManager: NavigationManager,
    private val repository: StockRepository,
    private val socketManager: FinnhubSocketManager
) : ViewModel() {

    fun navigateToStockDetail(stock: StockListItem) {
        navigationManager.navigate(Routes.stockDetail(stock))
    }

    fun navigateToRoboAI() {
        navigationManager.navigate(Routes.ROBO)
    }

    private val _uiState =
        MutableStateFlow<StockListUiState>(
            StockListUiState.Loading
        )

    val uiState = _uiState.asStateFlow()

    init {
        loadStocks()
    }

    private fun loadStocks() {
        viewModelScope.launch {

            _uiState.value = StockListUiState.Loading

            try {

                val items = repository.getStockListItems()

                _uiState.value =
                    StockListUiState.Success(items)

                observeSocket()

            } catch (e: Exception) {

                _uiState.value =
                    StockListUiState.Error(
                        e.message ?: "Unknown error"
                    )
            }
        }
    }

    private fun observeSocket() {

        viewModelScope.launch {

            try {
                socketManager
                    .observeTrades(
                        listOf(
                            "AAPL",
                            "MSFT",
                            "GOOGL",
                            "AMZN",
                            "TSLA"
                        )
                    )
                    .collect { trade ->

                        Log.d(
                            "SOCKET",
                            "${trade.symbol} : ${trade}"
                        )

                        updateStockPrice(
                            trade.symbol,
                            trade.price
                        )
                    }
            } catch (e: Exception) {
                Log.e("StockViewModel", "Socket connection error", e)
            }
        }
    }

    private fun updateStockPrice(
        symbol: String,
        price: Double
    ) {

        val currentState =
            _uiState.value

        if (
            currentState
                    !is StockListUiState.Success
        ) {
            return
        }

        val updatedStocks =
            currentState.stocks.map {

                if (it.symbol == symbol) {

                    val change =
                        price - it.previousClose

                    val percent =
                        if (it.previousClose != 0.0) {
                            (change / it.previousClose) * 100
                        } else {
                            0.0
                        }

                    it.copy(
                        currentPrice = price,
                        high = maxOf(it.high, price),
                        low = if (it.low == 0.0) price else minOf(it.low, price),
                        change = price - it.previousClose,
                        changePercent = percent
                    )

                } else {
                    it
                }
            }

        _uiState.value =
            StockListUiState.Success(
                updatedStocks
            )
    }

}