package com.example.algonative.persentation.home

import com.example.algonative.domain.model.BulkInstrument

sealed interface StockListUiState {

    data object Loading : StockListUiState

    data class Success(
        val stocks: List<BulkInstrument>
    ) : StockListUiState

    data class Error(
        val message: String
    ) : StockListUiState
}
