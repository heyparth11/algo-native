package com.example.algonative.persentation.home2

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.algonative.R
import com.example.algonative.domain.model.StockListItem
import com.example.algonative.persentation.components.CommonTabs
import com.example.algonative.persentation.components.StockRowSkeleton
import com.example.algonative.persentation.navigation.M3BottomBar

//import com.example.algonative.persentation.home.components.MarketIndexItem

@Composable
fun MarketHomeScreen(
    viewModel: StockViewModel = hiltViewModel()
) {

    var selectedTab by rememberSaveable {
        mutableIntStateOf(0)
    }

    val tabs = listOf(
        "Trending",
        "Top Gainers",
        "Top Losers",
        "Top Volume"
    )

    val uiState by viewModel
        .uiState
        .collectAsStateWithLifecycle()

    var selectedTabMain by remember { mutableStateOf("market") }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            M3BottomBar(
                selectedTab = selectedTabMain,
                onTabSelected = { tab ->
                    if (tab == "robo") {
                        viewModel.navigateToRoboAI()
                    } else {
                        selectedTabMain = tab
                    }
                }
            )
        },
        containerColor = Color.White
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {

            item { Spacer(Modifier.height(16.dp)) }

            item { HomeTopBar() }

            item { Spacer(Modifier.height(30.dp)) }

            item { MarketIndicesRow() }

            item { Spacer(Modifier.height(16.dp)) }

            item { TodayPLCard() }

            item { Spacer(Modifier.height(23.dp)) }

            item { QuickActionsSection() }

            item { Spacer(Modifier.height(30.dp)) }

            item { StocksHeader() }

            item { Spacer(Modifier.height(10.dp)) }

            item {
                CommonTabs(
                    tabs = tabs,
                    selectedTab = selectedTab,
                    onTabSelected = {
                        selectedTab = it
                    }
                )
            }

            when (val state = uiState) {

                is StockListUiState.Loading -> {
                    items(5) {
                        StockRowSkeleton()
                    }
                }

                is StockListUiState.Success -> {

                    items(state.stocks) { stock ->
                        StockRow(
                            stock = stock,
                            onClick = {
                                viewModel.navigateToStockDetail(stock)
                            }
                        )
                    }
                }

                is StockListUiState.Error -> {

                    item {
                        Text(state.message)
                    }
                }
            }

            item {
                Spacer(Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun StockListLoading() {

    LazyColumn {

        items(5) {
            StockRowSkeleton()
        }
    }
}

@Composable
fun HomeTopBar() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(R.drawable.menu),
            contentDescription = null,
            modifier = Modifier.size(28.dp)
        )

        Spacer(Modifier.width(12.dp))

        Surface(
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
            shape = RoundedCornerShape(8.dp),
            color = Color.White,
            border = BorderStroke(
                width = 1.dp,
                color = Color(0xFFF5F5F5)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painterResource(R.drawable.home_search),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(Modifier.width(8.dp))

                Text(
                    "Search",
                    color = Color.Gray
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Box(
            modifier = Modifier.clickable(
                onClick = {
//                   Thread.sleep(10000)
                }
            )
        ) {
            BadgedBox(
                badge = {
                    Badge {
                        Text("2")
                    }
                }
            ) {
                Image(
                    painterResource(R.drawable.notification),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}


data class MarketIndex(
    val name: String,
    val value: String,
    val change: String,
    val isPositive: Boolean
)

@Composable
fun MarketIndicesRow() {

    val indices = listOf(
        MarketIndex(
            name = "Nifty 50",
            value = "22,419.60",
            change = "-93.90 (0.42%)",
            isPositive = false
        ),
        MarketIndex(
            name = "Bank Nifty",
            value = "48,250.25",
            change = "+120.50 (0.25%)",
            isPositive = true
        ),
        MarketIndex(
            name = "Sensex",
            value = "73,850.20",
            change = "-65.30 (0.08%)",
            isPositive = false
        ),
        MarketIndex(
            name = "Nifty IT",
            value = "38,420.10",
            change = "+210.00 (0.55%)",
            isPositive = true
        )
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(indices) { index ->
            MarketIndexItem(index)
        }
    }
}

@Composable
fun MarketIndexItem(
    index: MarketIndex
) {
    Column {

        Text(
            text = index.name,
            color = Color.Gray,
            fontSize = 14.sp
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = index.value,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = index.change,
                color = if (index.isPositive)
                    Color(0xFF0F9D58)
                else
                    Color.Red,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun TodayPLCard() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0, 0, 0, alpha = (0.09f * 255).toInt())
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                Icons.Default.DonutLarge,
                null
            )

            Spacer(Modifier.width(12.dp))

            Text(
                "Today's P&L",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.weight(1f))

            Text(
                "+93.90 (0.42%)",
                color = Color(0xFF0057FF),
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.width(8.dp))

            Icon(
                Icons.Default.KeyboardArrowRight,
                null
            )
        }
    }
}

@Composable
fun QuickActionsSection() {

    val actions = listOf(
        QuickAction("F&O", R.drawable.home_fo),
        QuickAction("IPO", R.drawable.home_ipo),
        QuickAction("Commodities", R.drawable.home_cd),
        QuickAction("MF", R.drawable.home_mf),
        QuickAction("Events", R.drawable.home_events)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        actions.forEach { action ->

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    tonalElevation = 2.dp,
                    modifier = Modifier.size(65.dp),
                    color = Color(
                        red = 0,
                        green = 62,
                        blue = 196,
                        alpha = (0.04f * 255).toInt()
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {

                        Image(
                            painter = painterResource(action.icon),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = action.title,
                    fontSize = 12.sp,
                    color = Color(0xFF101B29)
                )
            }
        }
    }
}

@Composable
fun StocksHeader() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            "Stocks",
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(Modifier.weight(1f))

        Text(
            "View All",
            color = Color(0xFF0057FF)
        )

        Icon(
            Icons.Default.KeyboardArrowRight,
            null,
            tint = Color(0xFF0057FF)
        )
    }
}

data class StockItem(
    val name: String,
    val exchange: String,
    val price: String,
    val change: String,
    val positive: Boolean,
    val dividend: Boolean = false
)

data class QuickAction(
    val title: String,
    val icon: Int
)

@Composable
fun StockRow(
    stock: StockListItem,
    onClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 14.dp
            )
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            modifier = Modifier.size(42.dp),
            shape = CircleShape,
            tonalElevation = 2.dp
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(stock.logoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = stock.companyName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
//                placeholder = painterResource(R.drawable.ic_stock_placeholder),
//                error = painterResource(R.drawable.ic_stock_placeholder)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                stock.companyName,
                fontWeight = FontWeight.SemiBold
            )

            Row {

                Text(
                    stock.exchange,
                    color = Color.Gray
                )

                if (false) {

                    Spacer(Modifier.width(8.dp))

                    Surface(
                        color = Color(0xFFE8F0FE),
                        shape = RoundedCornerShape(6.dp)
                    ) {

                        Text(
                            "Dividend",
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 2.dp
                            ),
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {

            Text(
                text = "${if (stock.changePercent > 0) "+" else ""}${"%.2f".format(stock.changePercent)}%",
                color = if (stock.changePercent > 0)
                    Color(0xFF0057FF)
                else
                    Color.Red,
                fontWeight = FontWeight.Bold
            )

            Text(
                "$${"%.2f".format(stock.currentPrice)}",
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun BottomNavigationBar() {

    NavigationBar {

        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = {
                Icon(Icons.Default.ShowChart, null)
            },
            label = {
                Text("Market")
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(Icons.Default.BookmarkBorder, null)
            },
            label = {
                Text("Watchlist")
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(Icons.Default.WorkOutline, null)
            },
            label = {
                Text("Portfolio")
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = {
                Icon(Icons.Default.ReceiptLong, null)
            },
            label = {
                Text("Order")
            }
        )
    }
}