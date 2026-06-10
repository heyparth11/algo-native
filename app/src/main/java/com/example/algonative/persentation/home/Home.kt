package com.example.algonative.persentation.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.algonative.persentation.components.StockListHomeScreen
import com.example.algonative.persentation.components.TodayPLCard
import com.example.algonative.persentation.home.components.HomeTopBar
import com.example.algonative.persentation.home.components.NiftyTopBar
import kotlinx.coroutines.launch


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {

    val tabs = listOf(
        "Trending", "Top volume", "Top gainers", "Top losers", "Top by market"
    )

    val pagerState = rememberPagerState(
        pageCount = { tabs.size })

    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            HomeTopBar(onMenuClick = {
                viewModel.openSidebar()
            }, onSearchClick = {
                viewModel.openSearch()
            }, onNotificationClick = {
                viewModel.openNotifications()
            })
        }) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(6.dp))
            }

            item {
                NiftyTopBar(
                    modifier = Modifier.padding(
                        start = 16.dp, top = 16.dp
                    )
                )
            }

            item {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    TodayPLCard(
                        onClick = {})

                    Spacer(modifier = Modifier.height(20.dp))

//                    QuickActionsRow(
//                        navController = navController
//                    )

                    Spacer(modifier = Modifier.height(22.dp))
                }
            }

            item {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "Stocks", style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.clickable {
                            viewModel.openStocks()
                        }, verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "View All", color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            item {

                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 16.dp,
                    divider = {},
                    indicator = {}) {

                    tabs.forEachIndexed { index, title ->

                        Tab(selected = pagerState.currentPage == index, onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }, text = {
                            Text(
                                title,
                                fontWeight = if (pagerState.currentPage == index) FontWeight.Bold
                                else FontWeight.Normal
                            )
                        })
                    }
                }
            }

            item {

                HorizontalPager(
                    state = pagerState, modifier = Modifier.height(300.dp)
                ) { page ->

                    StockListHomeScreen(
                        tab = tabs[page],
                    )
                }
            }
        }
    }
}


data class HomeAction(
    val title: String, @DrawableRes val icon: Int, val route: String
)

//@Composable
//fun QuickActionsRow(
//    navController: NavController
//) {
//
//    val actions = listOf(
//        HomeAction("F&O", R.drawable.home_fo, "fno-home"),
//        HomeAction("IPO", R.drawable.home_ipo, "ipo"),
//        HomeAction("Commodities", R.drawable.home_cd, "commodities"),
//        HomeAction("MF", R.drawable.home_mf, "mf-home"),
//        HomeAction("Events", R.drawable.home_events, "events")
//    )
//
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.SpaceBetween
//    ) {
//
//        actions.forEach { action ->
//
//            HomeActionItem(
//                action = action,
//                onClick = {
//                    navController.navigate(action.route)
//                }
//            )
//        }
//    }
//}