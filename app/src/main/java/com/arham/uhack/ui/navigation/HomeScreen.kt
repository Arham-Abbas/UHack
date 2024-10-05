package com.arham.uhack.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.arham.uhack.ui.generateQRCode

@Composable
fun HomeScreen() {
    val qrCodeBitmap = generateQRCode("abbasrizvisayedarham6@gmail.com", 512, 512)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally // Center horizontally
    ) {
        Image(bitmap = qrCodeBitmap.asImageBitmap(),
            contentDescription = "QR Code",
            modifier = Modifier
                .size(256.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(16.dp)) // Add spacing
        Text("Team ID: 12345") // Replace with actual data
        Text("Team Name: Awesome Team") // Replace with actual data
        Text("Member Name: John Doe") // Replace with actual data
    }
}