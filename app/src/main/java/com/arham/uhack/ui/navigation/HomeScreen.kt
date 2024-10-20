package com.arham.uhack.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arham.uhack.data.FirestoreSyncManager
import com.arham.uhack.data.QRCode
import com.google.firebase.auth.FirebaseAuth
import com.arham.uhack.R
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.style.TextAlign

@SuppressLint("MutableCollectionMutableState")
@Composable
fun HomeScreen(firestoreSyncManager: FirestoreSyncManager) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val context = LocalContext.current
    var type by remember { mutableStateOf<String?>(null) } // Initialize as null
    var assignedTeams by remember { mutableStateOf<HashMap<String, List<String>>?>(null) }

    LaunchedEffect(firestoreSyncManager.type) {
        firestoreSyncManager.type.collect { newType ->
            type = newType
            firestoreSyncManager.update()
        }
    }

    // Add LaunchedEffect for assignedTeams
    LaunchedEffect(firestoreSyncManager.assignedTeams) {
        firestoreSyncManager.assignedTeams.collect { newAssignedTeams ->
            assignedTeams = newAssignedTeams
        }
    }

    val qrCodeBitmap = QRCode.qrCodeBitmap
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Added padding to the entire screen
            .verticalScroll(rememberScrollState()), // Make the Column scrollable
        horizontalAlignment = Alignment.Start // Left aligned
    ) {
        // Box for QR Code, User type, and User name
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center), // Center align content within the box
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp), // Added padding inside the card
                horizontalAlignment = Alignment.CenterHorizontally // Center align content within the column
            ) {
                if (qrCodeBitmap != null) {
                    Image(
                        bitmap = qrCodeBitmap.asImageBitmap(),
                        contentDescription = context.getString(R.string.description_qr),
                        modifier = Modifier
                            .size(256.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
                Spacer(modifier = Modifier.height(8.dp)) // Added spacing

                // App Name (Prominent)
                Text(
                    text = context.getString(R.string.event_name), // Assuming you have an app_name string resource
                    style = MaterialTheme.typography.headlineMedium, // Prominent font style
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // Prominent color
                    textAlign = TextAlign.Center // Center align
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = type.toString(),
                    style = MaterialTheme.typography.titleMedium, // More prominent font style
                    color = MaterialTheme.colorScheme.onSurfaceVariant // More prominent color
                )
                if (user != null) {
                    Text(
                        text = user.displayName.toString(),
                        style = MaterialTheme.typography.bodyMedium, // Apply font style
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Apply color
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Added spacing between boxes

        if (type == context.getString(R.string.type_mentors)) {
            Text(
                text = context.getString(R.string.field_assigned_teams), // Assuming you have an assigned_teams string resource
                style = MaterialTheme.typography.headlineSmall, // Prominent font style
                color = MaterialTheme.colorScheme.onSurface, // Prominent color
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp), // Added padding
                textAlign = TextAlign.Center // Center align
            )
        }
        // Display assigned teams in the order: Round 1, Round 2, Round 3
        val rounds = listOf(context.getString(R.string.key_round1), context.getString(R.string.key_round2), context.getString(R.string.key_round3))
        rounds.forEach { round ->
            assignedTeams?.get(round)?.let { teams ->
                Text(
                    text = round,
                    style = MaterialTheme.typography.titleMedium, // More prominent font style
                    color = MaterialTheme.colorScheme.onSurface, // More prominent color
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp), // Added bottom padding
                    textAlign = TextAlign.Center // Center align the round label
                )
                teams.forEach { team ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 16.dp), // Increased padding
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = team,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(16.dp) // Added padding inside the team card
                        )
                    }
                }
            }
        }
    }
}
