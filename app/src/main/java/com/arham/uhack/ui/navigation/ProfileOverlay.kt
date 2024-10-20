package com.arham.uhack.ui.navigation

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import coil.compose.AsyncImage
import com.arham.uhack.R
import com.google.firebase.auth.FirebaseAuth
import com.arham.uhack.LoginActivity
import kotlinx.coroutines.launch

@Composable
fun ProfileOverlay(showOverlay: Boolean, onDismiss: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    val context = LocalContext.current // Get the context
    val uriHandler = LocalUriHandler.current
    val coroutineScope = rememberCoroutineScope()
    val activity = (LocalContext.current as ComponentActivity)
    
    if (showOverlay) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.5f))
                .clickable { onDismiss() } // Close overlay on click outside
        ) {
            // Content of the overlay screen
            Card(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .wrapContentSize() // Allow the card to wrap its content
                    .padding(
                        horizontal = 32.dp,
                        vertical = 128.dp
                    ), // Added padding to the external card
                shape = RoundedCornerShape(16.dp), // Add rounded corners to the external card
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Box {
                    IconButton(
                        onClick = { onDismiss() }, // Call onDismiss when the icon is clicked
                        modifier = Modifier
                            .align(Alignment.TopStart) // Position at top start
                            .padding(8.dp) // Add padding around the icon
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close, // Use the Close icon
                            contentDescription = context.getString(R.string.description_close),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant // Set icon color
                        )
                    }
                    Column(modifier = Modifier.padding(16.dp)) {
                        // App Name (Prominent)
                        Text(
                            text = context.getString(R.string.app_name), // Assuming you have an app_name string resource
                            style = MaterialTheme.typography.headlineMedium, // Prominent font style
                            color = MaterialTheme.colorScheme.onSurfaceVariant, // Prominent color
                            textAlign = TextAlign.Center, // Center align
                            modifier = Modifier.fillMaxWidth() // Make it fill the width
                        )

                        Spacer(modifier = Modifier.height(8.dp)) // Add spacing below app name

                        // User profile card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            shape = RoundedCornerShape(16.dp), // Add rounded corners to the internal card
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) { // Added padding to the internal card
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp), // Added bottom padding to the Row
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // User profile picture
                                    if (user?.photoUrl != null) {
                                        AsyncImage(
                                            model = user.photoUrl,
                                            contentDescription = context.getString(R.string.description_profile_image),
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(CircleShape),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Rounded.AccountCircle,
                                            contentDescription = context.getString(R.string.description_profile_image),
                                            modifier = Modifier
                                                .size(48.dp),
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    // User name and email
                                    Column {
                                        Text(
                                            user?.displayName ?: "",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            user?.email ?: "",
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                                // Horizontal divider
                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                )
                                // Logout button
                                Button(
                                    onClick = {
                                        val intent = Intent(context, LoginActivity::class.java)
                                        context.startActivity(intent)
                                        val credentialManager = CredentialManager.create(context)

                                        coroutineScope.launch {
                                            credentialManager.clearCredentialState(ClearCredentialStateRequest())
                                        }

                                        auth.signOut()
                                        activity.finish()
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.Logout,
                                        contentDescription = context.getString(R.string.button_signout),
                                        modifier = Modifier.size(ButtonDefaults.IconSize)
                                    )
                                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                    Text(context.getString(R.string.button_signout))
                                }
                            }
                        }
                        // Privacy Policy and Terms of Service links
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp), // Add padding above the links
                            horizontalArrangement = Arrangement.Center // Center the links
                        ) {
                            Text(
                                text = context.getString(R.string.description_privacy_policy),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.clickable {
                                    uriHandler.openUri(context.getString(R.string.website_privacy_policy))
                                }
                            )
                            Text(
                                text = " • ", // Dot separator
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = context.getString(R.string.description_terms_of_service),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.clickable {
                                    uriHandler.openUri(context.getString(R.string.website_terms_of_service))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
