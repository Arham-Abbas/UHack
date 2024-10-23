package com.arham.uhack.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Eco
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Scale
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.arham.uhack.R
import com.arham.uhack.data.FirestoreSyncManager
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.arham.uhack.data.Normalizer
import kotlinx.coroutines.launch

@Composable
fun LeaderboardScreen(firestoreSyncManager: FirestoreSyncManager) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val items = listOf(
        context.getString(R.string.field_total),
        context.getString(R.string.category_technicality),
        context.getString(R.string.category_innovation),
        context.getString(R.string.category_scalability),
        context.getString(R.string.category_design),
        context.getString(R.string.category_sustainability)
    )
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { items.size })

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEachIndexed { _, screen ->
                    val icon = when (screen) {
                        context.getString(R.string.field_total) -> Icons.Rounded.Assessment
                        context.getString(R.string.category_technicality) -> Icons.Rounded.Build
                        context.getString(R.string.category_innovation) -> Icons.Rounded.Lightbulb
                        context.getString(R.string.category_scalability) -> Icons.Rounded.Scale
                        context.getString(R.string.category_design) -> Icons.Rounded.Palette
                        context.getString(R.string.category_sustainability) -> Icons.Rounded.Eco
                        else -> Icons.Rounded.Assessment // Default icon
                    }

                    NavigationBarItem(
                        icon = { androidx.compose.material3.Icon(icon, contentDescription = screen) },
                        modifier = Modifier.weight(1f), // Distribute space equally
                        label = {
                            Text(
                                text = screen,
                                maxLines = 1, // Limit to one line
                                overflow = TextOverflow.Ellipsis // Truncate with ellipsis
                            )
                        },
                        selected = currentRoute == screen,
                        onClick = {
                            navController.navigate(screen) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                // Update pager state based on current route
                LaunchedEffect(key1 = currentRoute) {
                    val index = items.indexOf(currentRoute)
                    if (index != -1) {
                        pagerState.animateScrollToPage(index)
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController = navController, startDestination = context.getString(R.string.field_total), modifier = Modifier.padding(innerPadding)) {
            composable(context.getString(R.string.field_total)) { CategoryView(firestoreSyncManager, context.getString(R.string.field_total)) }
            composable(context.getString(R.string.category_technicality)) { CategoryView(firestoreSyncManager, context.getString(R.string.category_technicality)) }
            composable(context.getString(R.string.category_innovation)) { CategoryView(firestoreSyncManager, context.getString(R.string.category_innovation)) }
            composable(context.getString(R.string.category_scalability)) { CategoryView(firestoreSyncManager, context.getString(R.string.category_scalability)) }
            composable(context.getString(R.string.category_design)) { CategoryView(firestoreSyncManager, context.getString(R.string.category_design)) }
            composable(context.getString(R.string.category_sustainability)) { CategoryView(firestoreSyncManager, context.getString(R.string.category_sustainability)) }
        }
    }
}

@Composable
fun CategoryView(firestoreSyncManager: FirestoreSyncManager, category: String) {
    // Load and display data for the specific category
    // You can access firestoreSyncManager.marks here to get the data
    // and display it using LazyColumn or other
    val context = LocalContext.current
    val normalizer = Normalizer()
    var marks by remember { mutableStateOf<Map<String, Map<String, Map<String, Int>>>?>(null) }
    var mentors by remember { mutableStateOf<Map<String, Map<String, List<String>>>?>(null) }
    var normalizedMarks by remember {
        mutableStateOf<Map<String, Map<String, Map<String, Double>>>?>(
            null
        )
    }

    LaunchedEffect(key1 = firestoreSyncManager.marks, key2 = firestoreSyncManager.mentors) {
        launch {
            firestoreSyncManager.marks.collect { newMarks ->
                marks = newMarks
                if (mentors != null) {
                    normalizedMarks = normalizer.normalize(marks!!, mentors!!)
                }
            }
        }
        launch {
            firestoreSyncManager.mentors.collect { newMentors ->
                mentors = newMentors
                if (marks != null) {
                    normalizedMarks = normalizer.normalize(marks!!, mentors!!)
                }
            }
        }
    }

    firestoreSyncManager.loadCollection(context.getString(R.string.collection_marking))
    firestoreSyncManager.loadCollection(context.getString(R.string.collection_mentors))

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        val sortedMarks = normalizedMarks?.entries?.sortedByDescending { (_, rounds) ->
            val totalScore = rounds.filterKeys { it in listOf(context.getString(R.string.key_round1), context.getString(R.string.key_round2), context.getString(R.string.key_round3)) }
                .values
                .sumOf { it[category] ?: 0.0 } // Sum scores for specified rounds and category
            totalScore
        }

        sortedMarks?.forEach { (teamId, _) ->
            // Display Team ID and total score for the specified category
            val totalScore = normalizedMarks?.get(teamId)
                ?.filterKeys { it in listOf(context.getString(R.string.key_round1), context.getString(R.string.key_round2), context.getString(R.string.key_round3)) }
                ?.values
                ?.sumOf { it[category] ?: 0.0 }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = teamId,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = totalScore?.toString() ?: "-", // Handle null totalScore
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}