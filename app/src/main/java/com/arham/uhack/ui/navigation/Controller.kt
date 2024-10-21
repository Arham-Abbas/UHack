package com.arham.uhack.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var type by remember { mutableStateOf<String?>(null) } // Initialize as null

    LaunchedEffect(firestoreSyncManager.type) {
        firestoreSyncManager.type.collect { newType ->
            type = newType
            firestoreSyncManager.update()
        }
    }

    // Create a derived state that depends on 'type'
    val showMarkingScreen by remember(type) {
        derivedStateOf { type == context.getString(R.string.type_mentors) }
    }

    LaunchedEffect(showMarkingScreen) {
        if (!showMarkingScreen) {
            navController.navigate(context.getString(R.string.route_home))
        }
    }

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
        composable(context.getString(R.string.route_about)) { AboutScreen() }
        composable(context.getString(R.string.route_feedback)) { FeedbackScreen() }
    }
}