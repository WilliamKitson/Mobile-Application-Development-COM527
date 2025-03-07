package com.example.a4kitsw10com527

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavigationComposable(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(navController=navController, startDestination="map") {
        composable("map") {
            MapComposable(modifier, navController)
        }

        composable("addLocation") {
            AddLocationComposable(modifier, navController)
        }
    }
}