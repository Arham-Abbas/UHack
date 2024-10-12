package com.arham.uhack.ui.navigation

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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.arham.uhack.data.FirestoreSyncManager
import com.arham.uhack.data.generateQRCode
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(firestoreSyncManager: FirestoreSyncManager) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var type by remember { mutableStateOf<String?>(null) } // Initialize as null

    LaunchedEffect(firestoreSyncManager) {
        firestoreSyncManager.type.collect { newType ->
            type = newType
        }
    }

    val qrCodeBitmap = user?.let { generateQRCode(it.uid, 512, 512) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
    ) {
        if (qrCodeBitmap != null) {
            Image(bitmap = qrCodeBitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier
                    .size(256.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
        }
        Spacer(modifier = Modifier.height(16.dp)) // Add spacing
        Text(type.toString())
        if (user != null) {
            Text("Name: ${user.displayName}")
        }
        if (type != "mentors") {
            Text("Team ID: 12345")
            Text("Team Name: Awesome Team")
        }
    }
}