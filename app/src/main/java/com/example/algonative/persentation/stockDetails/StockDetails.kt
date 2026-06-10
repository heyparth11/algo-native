package com.example.algonative.persentation.stockDetails

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.TabPosition
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.algonative.domain.model.StockListItem
import com.example.algonative.persentation.components.CommonTabs2

@Composable
fun StockDetailsScreen(
    stock: StockListItem,
    onBackClick: () -> Unit,
    viewModel: StockDetailViewModel = hiltViewModel()
) {

    val uiState by viewModel
        .uiState
        .collectAsStateWithLifecycle()

    val stock by viewModel
        .stock
        .collectAsStateWithLifecycle()

    val candles by viewModel
        .candles
        .collectAsStateWithLifecycle()

    var selectedTab by rememberSaveable {
        mutableIntStateOf(0)
    }

    val tabs = listOf(
        "Chart",
        "Stock Info",
        "F&O",
        "News"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {

            when (uiState) {

                StockDetailUiState.Loading -> {
                    item { CircularProgressIndicator() }
                }

                StockDetailUiState.Success -> {

                    stock?.let {

                        item { StockDetailsTopBar(stock = it, onBackClick = onBackClick) }

                        item {
                            CommonTabs2(
                                tabs = tabs,
                                selectedTab = selectedTab,
                                onTabSelected = {
                                    selectedTab = it
                                }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(24.dp)) }

                        item { PriceSection(stock = it) }

                        item {
                            StockJsonChart(
                                rawHistoryPoints = candles,
                                lineColor = Color(0xFF2172E5)
                            )
                        }

                        item { TimeRangeSelector() }

                        item { CreateSipCard() }

                        item { Spacer(modifier = Modifier.height(15.dp)) }

                        item { MarketDepthSection() }

                        item { PerformanceSection(stock = it) }

                    }
                }

                is StockDetailUiState.Error -> {
                    item { Text("Error") }
                }
            }
        }
        BuySellButtons(modifier = Modifier.align(Alignment.BottomCenter))

    }

}

@Composable
fun StockDetailsTopBar(
    stock: StockListItem,
    onBackClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, end = 16.dp, bottom = 16.dp, start = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
        }

        Text(
            text = stock.companyName,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = {}) {
            Icon(Icons.Default.NotificationsNone, null)
        }

        IconButton(onClick = {}) {
            Icon(Icons.Default.BookmarkBorder, null)
        }
    }
}

@Composable
fun PriceSection(stock: StockListItem) {

    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                "$${"%.2f".format(stock.currentPrice)}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.width(8.dp))

            Text(
                stock.exchange,
                color = Color(0xFF0057FF),
                fontWeight = FontWeight.Medium
            )
        }

        Row {
            val isPositive = stock.change >= 0
            val color = if (isPositive) Color(0xFF0057FF) else Color.Red
            val sign = if (isPositive) "+" else ""

            Text(
                "$sign${"%.2f".format(stock.change)} ($sign${"%.2f".format(stock.changePercent)}%)",
                color = color
            )

            Spacer(Modifier.width(8.dp))

            Text(
                "1D",
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TimeRangeSelector() {

    val items = listOf(
        "1D",
        "1W",
        "1M",
        "1Y",
        "3Y",
        "ALL"
    )

    var selected by remember {
        mutableIntStateOf(0)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        items.forEachIndexed { index, title ->

            Surface(
                color = if (selected == index)
                    Color(0xFFE8EEFF)
                else
                    Color.Transparent,
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    selected = index
                }
            ) {

                Text(
                    title,
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
                )
            }
        }
    }
}

@Composable
fun CreateSipCard(
    onClick: () -> Unit = {}
) {

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            Color(0xFFE5E7EB)
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 14.dp,
                    vertical = 13.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = Color(0xFF0F4BD7),
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Create Stock SIP",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Automate investments to grow wealth!",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF374151),
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun MarketDepthSection() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {

        Header()

        Spacer(Modifier.height(20.dp))

        MarketDepthTable()

        Spacer(Modifier.height(24.dp))

        TotalsRow()

        Spacer(Modifier.height(24.dp))

        BuySellRatio()
    }
}

@Composable
private fun HeaderRow() {

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text(
            "Bid",
            modifier = Modifier.weight(1.2f)
        )

        Text(
            "Orders",
            modifier = Modifier.weight(1f)
        )

        Text(
            "Qty",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )

        Text(
            "Offer",
            modifier = Modifier.weight(1.2f)
        )

        Text(
            "Orders",
            modifier = Modifier.weight(1f)
        )

        Text(
            "Qty",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun MarketDepthRow() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "1865.85",
            color = Color(0xFF0F4BD7),
            modifier = Modifier.weight(1.2f)
        )

        Text(
            text = "12",
            color = Color(0xFF0F4BD7),
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    Color(0xFF0F4BD7).copy(alpha = 0.08f),
                    RoundedCornerShape(8.dp)
                )
                .padding(
                    horizontal = 10.dp,
                    vertical = 4.dp
                )
        ) {
            Text(
                text = "6372",
                color = Color(0xFF0F4BD7),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = "1865.85",
            color = Color(0xFFFF4B5C),
            modifier = Modifier.weight(1.2f)
        )

        Text(
            text = "12",
            color = Color(0xFFFF4B5C),
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .background(
                    Color(0xFFFF4B5C).copy(alpha = 0.08f),
                    RoundedCornerShape(8.dp)
                )
                .padding(
                    horizontal = 10.dp,
                    vertical = 4.dp
                )
        ) {
            Text(
                text = "6372",
                color = Color(0xFFFF4B5C),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
private fun TotalsRow() {

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {

        Text("Bid total")

        Spacer(Modifier.weight(1f))

        Text(
            "2,26,470",
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.width(20.dp))

        Text("Ask total")

        Spacer(Modifier.weight(1f))

        Text(
            "2,26,470",
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun BuySellRatio() {

    Column {

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Buy orders")

            Spacer(Modifier.weight(1f))

            Text("Sell orders")
        }

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(
                    Color.LightGray.copy(alpha = 0.2f),
                    RoundedCornerShape(50)
                )
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .fillMaxHeight()
                    .background(
                        Color(0xFF0F4BD7),
                        RoundedCornerShape(50)
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .background(
                        Color(0xFFFF4B5C),
                        RoundedCornerShape(50)
                    )
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(
                "80.07%",
                color = Color.Gray
            )

            Spacer(Modifier.weight(1f))

            Text(
                "20.07%",
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun MarketDepthTable() {

    Column {

        HeaderRow()

        Spacer(Modifier.height(12.dp))

        repeat(5) {
            MarketDepthRow()
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun Header() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = "Markets Depth",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "👑",
            fontSize = 20.sp
        )

        Spacer(Modifier.width(8.dp))

        Text(
            text = "Show 100 depth",
            color = Color(0xFF0F4BD7),
            fontSize = 13.sp
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF0F4BD7)
        )
    }
}

@Composable
fun PerformanceSection(stock: StockListItem) {

    Column(
        modifier = Modifier.padding(20.dp)
    ) {

        Text(
            "Performance",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )

        Spacer(Modifier.height(20.dp))

        val todayRange = stock.high - stock.low
        val progress = if (todayRange > 0.0) {
            ((stock.currentPrice - stock.low) / todayRange).toFloat().coerceIn(0f, 1f)
        } else {
            0.5f
        }

        PerformanceBar(
            title = "Today's Low",
            low = "$${"%.2f".format(stock.low)}",
            high = "$${"%.2f".format(stock.high)}",
            progress = progress
        )

        Spacer(Modifier.height(20.dp))

        PerformanceBar(
            title = "52 Week Low",
            low = "830.23",
            high = "1123.54",
            progress = 0.65f
        )
    }
}

@Composable
fun PerformanceBar(
    title: String,
    low: String,
    high: String,
    progress: Float
) {

    Column {
        Text(
            title,
            color = Color.Gray,
            fontSize = 14.sp
        )

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            Text(low)

            Spacer(Modifier.weight(1f))

            Text(high)
        }

        Spacer(Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun BuySellButtons(
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier.background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {

            Button(
                onClick = {},
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F4BD7)
                )
            ) {
                Text(
                    "BUY",
                    fontSize = 16.sp
                )
            }

            Button(
                onClick = {},
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF3A4A)
                )
            ) {
                Text(
                    "SELL",
                    fontSize = 16.sp
                )
            }
        }
    }
}

fun Modifier.customTabIndicatorOffset(
    currentTabPosition: TabPosition,
    tabWidth: Dp
): Modifier = composed {
    val currentTabWidth by animateDpAsState(
        targetValue = tabWidth,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "tabWidth"
    )
    val indicatorOffset by animateDpAsState(
        targetValue = currentTabPosition.left + 16.dp,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing),
        label = "tabOffset"
    )
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(currentTabWidth)
}