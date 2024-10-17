package com.arham.uhack.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.arham.uhack.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

@Composable
fun AboutScreen() {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp) // Added spacing between items
    ) {
        // United Group of Institutions
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = context.getString(R.string.heading_united),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.onPrimaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) // Rounded corners
                        .padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.united),
                        contentDescription = context.getString(R.string.description_united),
                        modifier = Modifier.size(200.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) // Rounded corners, light background
                        .padding(8.dp)
                        .clickable { uriHandler.openUri(context.getString(R.string.link_united)) }
                ) {
                    LinkWithIcon(
                        icon = Icons.Rounded.Language,
                        text = context.getString(R.string.link_united),
                        onClick = { uriHandler.openUri(context.getString(R.string.link_united)) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = context.getString(R.string.about_united),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp)) // Add spacing between drawable groups

        // Technovators Society
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = context.getString(R.string.heading_technovators),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.onPrimaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) // Rounded corners
                        .padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.technovators),
                        contentDescription = context.getString(R.string.description_technovators),
                        modifier = Modifier.size(200.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) // Rounded corners, light background
                        .padding(8.dp)
                        .clickable { uriHandler.openUri(context.getString(R.string.link_technovators)) }
                ) {
                    LinkWithIcon(
                        icon = Icons.Rounded.Language,
                        text = context.getString(R.string.link_technovators),
                        onClick = { uriHandler.openUri(context.getString(R.string.link_technovators)) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = context.getString(R.string.about_technovators),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp)) // Add spacing between drawable groups

        // UHack 3.0
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = context.getString(R.string.event_name),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.onPrimaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) // Rounded corners
                        .padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.uhack),
                        contentDescription = context.getString(R.string.description_uhack),
                        modifier = Modifier.size(200.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) // Rounded corners, light background
                        .padding(8.dp)
                        .clickable { uriHandler.openUri(context.getString(R.string.link_uhack)) }
                ) {
                    LinkWithIcon(
                        icon = Icons.Rounded.Language,
                        text = context.getString(R.string.link_uhack),
                        onClick = { uriHandler.openUri(context.getString(R.string.link_uhack)) }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = context.getString(R.string.about_uhack),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

    @Composable
    fun LinkWithIcon(
        icon: ImageVector,
        text: String,
        onClick: () -> Unit
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onClick)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary // Dynamic color
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge, // Increased text size
                color = MaterialTheme.colorScheme.primary, // Dynamic color
                textDecoration = TextDecoration.Underline // Added underline
            )
        }
    }