package com.example.algonative.persentation.navigation

import androidx.navigation.NavHostController
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor() {

    private var navController: NavHostController? = null

    fun setController(controller: NavHostController) {
        navController = controller
    }

    fun navigate(route: String) {
        navController?.navigate(route)
    }

    fun popBackStack() {
        navController?.popBackStack()
    }

    fun navigateAndClear(route: String) {
        navController?.navigate(route) {
            popUpTo(0)
        }
    }
}