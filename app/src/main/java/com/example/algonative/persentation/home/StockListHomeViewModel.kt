package com.example.algonative.persentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.algonative.domain.InstrumentApi
import com.example.algonative.domain.SubscriptionApi
import com.example.algonative.domain.model.BulkInstrument
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockListHomeViewModel @Inject constructor(
    private val instrumentApi: InstrumentApi,
    private val subscriptionApi: SubscriptionApi
) : ViewModel() {

    private val _uiState = MutableStateFlow<StockListUiState>(StockListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var subscribedStocks = emptyList<Map<String, Any>>()

    fun loadStocks(tab: String) {
        viewModelScope.launch {
            _uiState.value = StockListUiState.Loading
            try {
                val stocks = instrumentApi.getCategorizedEQ(tab)
                subscribe(stocks)
                _uiState.value = StockListUiState.Success(stocks)
            } catch (e: Exception) {
                _uiState.value = StockListUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    private suspend fun subscribe(stocks: List<BulkInstrument>) {
        subscribedStocks = stocks.map {
            mapOf(
                "exchangeSegment" to it.exchangeSegment,
                "exchangeInstrumentID" to it.exchangeInstrumentID
            )
        }
        subscriptionApi.subscribeBulkInstrument(subscribedStocks)
    }

    override fun onCleared() {
        viewModelScope.launch {
            subscriptionApi.unsubscribeBulkInstrument(subscribedStocks)
        }
        super.onCleared()
    }
}
