package com.example.exoplayerdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.core.net.toUri
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.exoplayerdemo.nav.NavRoutes
import com.example.exoplayerdemo.screen.VideoListScreen
import com.example.exoplayerdemo.screen.VideoScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = NavRoutes.LIST) {
                    composable(NavRoutes.LIST) {
                        VideoListScreen(navController)
                    }
                    composable(
                        "video?uri={uri}",
                        arguments = listOf(navArgument("uri") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val uriString = backStackEntry.arguments?.getString("uri") ?: ""
                        VideoScreen(videoUri = uriString.toUri(), navController = navController)
                    }
                }
            }
        }
    }
}