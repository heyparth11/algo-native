package com.example.algonative.persentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.algonative.persentation.navigation.AppNavGraph

@Composable
fun RootScaffold(
    navController: NavHostController,
) {

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = Color.White
    ) { padding ->

        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(padding)
        )
    }
}
