package com.example.algonative.persentation.home2

import com.example.algonative.domain.model.StockListItem

sealed interface StockListUiState {

    data object Loading : StockListUiState

    data class Success(
        val stocks: List<StockListItem>
    ) : StockListUiState

    data class Error(
        val message: String
    ) : StockListUiState
}