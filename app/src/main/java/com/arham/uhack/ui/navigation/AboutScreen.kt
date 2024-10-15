package com.arham.uhack.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // First Icon and Text
        IconWithText(
            icon = Icons.Rounded.Info, // Replace with your desired icon
            contentDescription = "About Icon",
            text = "This is the about section of the app."
        )

        Spacer(modifier = Modifier.height(32.dp)) // Add spacing between icon groups

        // Second Icon and Text
        IconWithText(
            icon = Icons.Rounded.Build, // Replace with your desired icon
            contentDescription = "Version Icon",
            text = "App version: 1.0.0" // Replace with your app version
        )
    }
}

@Composable
fun IconWithText(
    icon: ImageVector,
    contentDescription: String,
    text: String
) {
    Column(horizontalAlignment = Alignment.Start) { // Left-align text
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(48.dp) // Adjust icon size as needed
        )
        Spacer(modifier = Modifier.height(8.dp)) // Add spacing between icon and text
        Text(text)
    }
}