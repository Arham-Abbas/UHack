package com.arham.uhack.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arham.uhack.R
import com.arham.uhack.data.FirestoreSyncManager

@SuppressLint("MutableCollectionMutableState")
@Composable
fun TeamItem(
    team: String,
    firestoreSyncManager: FirestoreSyncManager,
    isExpanded: Boolean, // Add isExpanded parameter
    onExpand: () -> Unit, // Add onExpand lambda
    roundColor: Color,
    round: String
) {
    val context = LocalContext.current
    val categories = listOf(
        context.getString(R.string.category_technicality),
        context.getString(R.string.category_innovation),
        context.getString(R.string.category_scalability),
        context.getString(R.string.category_design),
        context.getString(R.string.category_sustainability)
    )
    val selectedOptions by remember(team) { mutableStateOf(mutableStateMapOf<String, Any>()) } // Make selectedOptions local to each TeamItem
    var isSubmitted by remember(team) { mutableStateOf(false) } // State to track submission, local to each TeamItem
    val isAllSelected = categories.all { selectedOptions.containsKey(it) } // Check if all categories have a selected option
    val totalMarks = selectedOptions.values.sumOf { it as Int }
    var total by remember(team) { mutableIntStateOf(0) } // Store total marks separately, local to each TeamItem
    var marks by remember { mutableStateOf<Map<String, Int>?>(null) } // Store team marks, local to each TeamItem

    LaunchedEffect(key1 = firestoreSyncManager.marks) {
        firestoreSyncManager.marks.collect { newMarks ->
            marks = if (newMarks != null) {
                newMarks[team]?.get(round)
            } // Get marks for the current team
            else {
                null
            }
            if (marks != null) {
                total = marks!![context.getString(R.string.field_total)] ?: 0 // Extract total marks
                selectedOptions.putAll(marks!!.filterKeys { it != context.getString(R.string.field_total) }) // Put remaining marks
                isSubmitted = true
            } else {
                selectedOptions.clear()
                isSubmitted = false
            }
        }
    }

    Column(Modifier.padding(8.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { onExpand() },
            colors = CardDefaults.cardColors(
                containerColor = roundColor
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = team,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.weight(1f))
                // Icon to indicate expansion state
                Icon(
                    imageVector = if (isExpanded) Icons.Rounded.ArrowDropUp else Icons.Rounded.ArrowDropDown,
                    contentDescription = if (isExpanded) context.getString(
                        R.string.description_collapse
                    ) else context.getString(
                        R.string.description_expand
                    ),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (isExpanded) {
            firestoreSyncManager.loadDocument(
                context.getString(R.string.collection_marking),
                team,
                round
            )
            categories.forEach { category ->
                Column {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    val selectedOption =
                        selectedOptions[category] ?: 0 // Default to 0 if not selected
                    Row {
                        for (i in 1..5) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedOption == i,
                                    onClick = {
                                        if (!isSubmitted) { // Only allow changes if not submitted
                                            selectedOptions[category] = i
                                        }
                                    },
                                    enabled = !isSubmitted, // Disable radio buttons if submitted
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.primary,
                                        unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        disabledSelectedColor = MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = 0.38f
                                        ),
                                        disabledUnselectedColor = MaterialTheme.colorScheme.onSurface.copy(
                                            alpha = 0.38f
                                        )
                                    )
                                )
                                Text(
                                    text = i.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
            Button(
                onClick = {
                    isSubmitted = true // Set submitted state to true
                    // Merge total marks into selectedOptions
                    val dataToSend = selectedOptions.toMutableMap()
                    dataToSend[context.getString(R.string.field_total)] =
                        totalMarks // Add total field

                    // Create a nested map to store marks for the round
                    val roundMarks = mutableMapOf<String, Any>()
                    roundMarks[round] = dataToSend

                    firestoreSyncManager.saveDocument(
                        context.getString(R.string.collection_marking),
                        team,
                        roundMarks
                    )
                },
                enabled = isAllSelected && !isSubmitted, // Enable only if all selected and not submitted
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                )
            ) {
                Text(
                    text = context.getString(R.string.button_submit),
                    style = MaterialTheme.typography.labelLarge
                )
            }
            HorizontalDivider()
        }
    }
}