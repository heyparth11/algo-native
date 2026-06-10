package com.example.algonative.persentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.algonative.domain.model.StockListItem
import com.example.algonative.persentation.home2.MarketHomeScreen
import com.example.algonative.persentation.stockDetails.StockDetailsScreen
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {

        composable(Routes.HOME) {
            MarketHomeScreen()
        }

        composable(Routes.SIDEBAR) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Sidebar Screen (Placeholder)")
            }
        }

        composable(Routes.SEARCH) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Search Screen (Placeholder)")
            }
        }

        composable(Routes.NOTIFICATIONS) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Notifications Screen (Placeholder)")
            }
        }

        composable(
            route = "stock-details/{stockJson}"
        ) { backStackEntry ->

            val stockJson =
                backStackEntry.arguments
                    ?.getString("stockJson")
                    .orEmpty()

            val decodedJson = URLDecoder.decode(stockJson, StandardCharsets.UTF_8.name())
            val stock = Json.decodeFromString<StockListItem>(decodedJson)

            StockDetailsScreen(
                stock = stock,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.ALL_STOCKS) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("All Stocks Screen (Placeholder)")
            }
        }

        composable(Routes.ROBO) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Robo AI Screen (Placeholder)")
            }
        }
    }
}