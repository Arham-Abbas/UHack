package com.arham.uhack.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.arham.uhack.R
import com.arham.uhack.data.FirestoreSyncManager

@Composable
fun NavigationHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    firestoreSyncManager: FirestoreSyncManager
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = context.getString(R.string.route_home),
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(context.getString(R.string.route_home)) {
            HomeScreen(
                firestoreSyncManager = firestoreSyncManager
            )
        }
        composable(context.getString(R.string.route_marking)) {
            MarkingScreen(
                firestoreSyncManager
            )
        }
        composable(context.getString(R.string.route_leaderboard)) {
            LeaderboardScreen(
                firestoreSyncManager
            )
        }
        composable(context.getString(R.string.route_about)) { AboutScreen() }
        composable(context.getString(R.string.route_feedback)) { FeedbackScreen() }
    }
}