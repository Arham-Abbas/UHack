package com.arham.uhack.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arham.uhack.R
import com.arham.uhack.data.FirestoreSyncManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp

@SuppressLint("MutableCollectionMutableState")
@Composable
fun MarkingScreen(firestoreSyncManager: FirestoreSyncManager) {
    var assignedTeams by remember { mutableStateOf<HashMap<String, List<String>>?>(null) }
    val context = LocalContext.current
    var expandedRound by remember { mutableStateOf<String?>(null) } // State to store expanded round
    var expandedTeam by remember { mutableStateOf<String?>(null) } // State to store expanded team


    LaunchedEffect(firestoreSyncManager.assignedTeams) {
        firestoreSyncManager.assignedTeams.collect { newAssignedTeams ->
            assignedTeams = newAssignedTeams
        }
    }

    val rounds = listOf(context.getString(R.string.key_round1), context.getString(R.string.key_round2), context.getString(R.string.key_round3))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        // Filter rounds with assigned teams
        val roundsWithTeams = rounds.filter { round ->
            assignedTeams?.get(round)?.isNotEmpty() == true
        }
        roundsWithTeams.forEach { round ->
            // Display round as clickable header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        expandedRound = if (expandedRound == round) null else round
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = round,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.weight(1f))
                    // Icon to indicate expansion state
                    Icon(
                        imageVector = if (expandedRound == round) Icons.Rounded.ArrowDropUp else Icons.Rounded.ArrowDropDown,
                        contentDescription = if (expandedRound == round) context.getString(
                            R.string.description_collapse
                        ) else context.getString(
                            R.string.description_expand
                        ),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Display teams for the expanded round
            if (expandedRound == round) {
                assignedTeams?.get(round)?.let { teams ->
                    teams.forEach { team ->
                        TeamItem(
                            team = team,
                            firestoreSyncManager = firestoreSyncManager,
                            isExpanded = expandedTeam == team, // Pass isExpanded state
                            onExpand = {
                                expandedTeam = if (expandedTeam == team) null else team // Update expandedTeam state
                            },
                            roundColor = MaterialTheme.colorScheme.primaryContainer, // Pass round color to TeamItem
                            round = round
                        )
                    }
                }
            }
        }
    }
}