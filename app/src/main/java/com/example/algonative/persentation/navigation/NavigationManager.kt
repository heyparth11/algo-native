package com.example.algonative.persentation.navigation

import androidx.navigation.NavHostController
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationManager @Inject constructor() {

    private var navControllerRef: WeakReference<NavHostController>? = null

    fun setController(controller: NavHostController) {
        navControllerRef = WeakReference(controller)
    }

    fun clearController(controller: NavHostController) {
        if (navControllerRef?.get() == controller) {
            navControllerRef = null
        }
    }

    fun navigate(route: String) {
        navControllerRef?.get()?.navigate(route)
    }

    fun popBackStack() {
        navControllerRef?.get()?.popBackStack()
    }

    fun navigateAndClear(route: String) {
        navControllerRef?.get()?.navigate(route) {
            popUpTo(0)
        }
    }
}