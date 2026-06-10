package com.example.algonative.persentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.algonative.domain.model.BulkInstrument
import com.example.algonative.persentation.home.StockListHomeViewModel
import com.example.algonative.persentation.home.StockListUiState
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.defaultShimmerTheme
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
fun StockListHomeScreen(
    tab: String,
    viewModel: StockListHomeViewModel = hiltViewModel()
) {

    val uiState by viewModel
        .uiState
        .collectAsStateWithLifecycle()

    LaunchedEffect(tab) {
        viewModel.loadStocks(tab)
    }

    when (val state = uiState) {

        StockListUiState.Loading -> {
            StockListLoading()
        }

        is StockListUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(state.message)
            }
        }

        is StockListUiState.Success -> {
            StockListContent(
                stocks = state.stocks
            )
        }
    }
}

@Composable
fun StockListContent(
    stocks: List<BulkInstrument>
) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        items(stocks.take(5)) { stock ->
            StockListSingleItem(
                stock = stock,
                onClick = {
                    // Navigate to details
                }
            )
        }
    }
}

@Composable
fun StockListSingleItem(
    stock: BulkInstrument,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stock.displayName.take(1),
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = stock.displayName,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stock.exchangeInstrumentID,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun StockListLoading() {
    LazyColumn {
        items(5) {
            StockLoadingItem()
        }
    }
}

@Composable
fun StockLoadingItem() {
    val shimmer = rememberShimmer(
        shimmerBounds = ShimmerBounds.Window,
        theme = defaultShimmerTheme
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .shimmer(shimmer)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(16.dp)
                    .shimmer(shimmer)
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(16.dp)
                    .shimmer(shimmer)
                    .background(Color.LightGray)
            )
        }
    }
}
