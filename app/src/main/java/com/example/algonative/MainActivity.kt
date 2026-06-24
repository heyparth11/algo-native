package com.example.algonative

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.example.algonative.persentation.RootScaffold
import com.example.algonative.persentation.navigation.NavigationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigationManager: NavigationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

//        setContent {
//            Button(
//                onClick = {
//                    finish()
//                },
//                modifier = Modifier.padding(top = 40.dp)
//            ) {
//                Text("Close Activity")
//            }
//        }

        setContent {
            val navController = rememberNavController()

            DisposableEffect(navController) {
                navigationManager.setController(navController)
                onDispose {
                    navigationManager.clearController(navController)
                }
            }
            RootScaffold(
                navController = navController
            )
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("LifeCycle", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("LifeCycle", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("LifeCycle", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("LifeCycle", "onStop")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("LifeCycle", "onRestart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LifeCycle", "onDestroy")
    }

}
