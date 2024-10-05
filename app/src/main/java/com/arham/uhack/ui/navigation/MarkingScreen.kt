package com.arham.uhack.ui.navigation

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Data store for preferences
val Context.dataStore by preferencesDataStore(name = "settings")

@Composable
fun MarkingScreen() {
    val teams = listOf(
        Team(1, "Team A"),
        Team(2, "Team B"),
        Team(3, "Team C"),
        Team(4, "Team D"),
        Team(5, "Team E")
    )
    val scope = rememberCoroutineScope()
    LazyColumn { // Use LazyColumn for scrolling
        items(teams.size) { index -> // Iterate through teams using index
            val team = teams[index]
            TeamItem(team, scope)
        }
    }
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun TeamItem(team: Team, scope: CoroutineScope) {
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf("Category A", "Category B", "Category C", "Category D", "Category E")
    var selectedOptions by remember { mutableStateOf(mutableStateMapOf<String, Int>()) }

    Column(Modifier.padding(8.dp)) {
        Row(Modifier.clickable { expanded = !expanded }) {
            Text("Team ID: ${team.id}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.weight(1f))
        }
        if (expanded) {
            categories.forEach { category ->
                Text(category, style = MaterialTheme.typography.bodyMedium)
                val selectedOption = selectedOptions[category] ?: 0 // Default to 0 if not selected
                Row {
                    for (i in 1..5) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedOption == i,
                                onClick = {
                                    selectedOptions[category] = i
                                    //scope.launch { saveSelectedOptions(team.id, selectedOptions.value) }
                                }
                            )
                            Text(i.toString())
                        }
                    }
                }
            }
        }
        HorizontalDivider()
    }
}

data class Team(val id: Int, val name: String)

// Function to save selected options using DataStore
/*suspend fun saveSelectedOptions(teamId: Int, options: Map<String, Int>) {
    val context = LocalContext.current
    context.dataStore.edit { settings ->
        options.forEach { (category, score) ->
            settings["team_${teamId}_${category}"] = score
        }
    }
}*/