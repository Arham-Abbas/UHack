package com.arham.uhack.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalLayoutApi::class)
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
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
    ) {
        if (qrCodeBitmap != null) {
            Image(bitmap = qrCodeBitmap.asImageBitmap(),
                contentDescription = context.getString(R.string.description_qr),
                modifier = Modifier
                    .size(256.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        }
        Spacer(modifier = Modifier.height(16.dp)) // Add spacing
        Text(type.toString())
        if (user != null) {
            Text(user.displayName.toString())
        }
        if (type == context.getString(R.string.type_mentors)) {
            assignedTeams?.forEach { (round, teams) -> // Iterate through assignedTeams
                Text(round) // Display the round (key)
                FlowRow( // Use FlowRow for wrapping
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // Spacing between items horizontally
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Spacing between rows vertically
                ) {
                    teams.forEach { team -> // Iterate through teams (values)
                        Text(text = team,
                            modifier = Modifier.padding(4.dp) // Optional: Add padding around each item
                        ) // Display each team name
                    }
                }
            }
        }
    }
}