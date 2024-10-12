package com.arham.uhack.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.arham.uhack.data.FirestoreSyncManager

@Composable
fun NavigationHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    firestoreSyncManager: FirestoreSyncManager
) {
    NavHost(
        navController = navController,
        startDestination = "Home",
        modifier = Modifier.padding(paddingValues)
    ) {
        composable("Home") { HomeScreen(firestoreSyncManager) }
        composable("Marking") { MarkingScreen(firestoreSyncManager) }
    }
}