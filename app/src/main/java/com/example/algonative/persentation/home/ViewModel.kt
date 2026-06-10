package com.example.algonative.persentation.home

import androidx.lifecycle.ViewModel
import com.example.algonative.persentation.navigation.NavigationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val navigationManager: NavigationManager
) : ViewModel() {

    fun openNotifications() {
        navigationManager.navigate("notifications")
    }

    fun openSearch() {
        navigationManager.navigate("search")
    }

    fun openSidebar() {
        navigationManager.navigate("sidebar")
    }

    fun openStocks() {
        navigationManager.navigate("all-stocks")
    }

    fun openPortfolio() {
        navigationManager.navigate("portfolio")
    }

    fun openOrders() {
        navigationManager.navigate("orders")
    }
}

