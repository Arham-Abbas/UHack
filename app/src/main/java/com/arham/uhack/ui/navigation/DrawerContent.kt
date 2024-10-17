package com.arham.uhack.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Feedback
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arham.uhack.R
import com.arham.uhack.data.FirestoreSyncManager

@Composable
fun DrawerContent(onDestinationClicked: (route: String) -> Unit, currentRoute: String, firestoreSyncManager: FirestoreSyncManager) {
    val context = LocalContext.current
    var type by remember { mutableStateOf<String?>(null) } // Initialize as null

    LaunchedEffect(firestoreSyncManager.type) {
        firestoreSyncManager.type.collect { newType ->
            type = newType
            firestoreSyncManager.update()
        }
    }

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface // Set drawer background color
    ) {
        Column {
            Text(
                text = context.getString(R.string.app_name),
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.headlineSmall, // Set app name typography
                color = MaterialTheme.colorScheme.onSurface // Set app name color
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(8.dp)) // Add padding after divider
            NavigationDrawerItem(
                icon = {
                    Icon(
                        Icons.Rounded.Home,
                        contentDescription = context.getString(R.string.route_home),
                        tint = if (currentRoute == context.getString(R.string.route_home)) {
                            MaterialTheme.colorScheme.primary // Tint with primary color when selected
                        } else {
                            MaterialTheme.colorScheme.onSurface // Default tint color
                        }
                    )
                }, // Add Home icon
                label = {
                    Text(
                        text = context.getString(R.string.route_home),
                        style = MaterialTheme.typography.bodyMedium, // Set label typography
                        color = if (currentRoute == context.getString(R.string.route_home)) {
                            MaterialTheme.colorScheme.primary // Highlight selected item
                        } else {
                            MaterialTheme.colorScheme.onSurface // Default label color
                        }
                    )
                },
                selected = currentRoute == context.getString(R.string.route_home),
                onClick = { onDestinationClicked(context.getString(R.string.route_home)) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
            if (type == context.getString(R.string.type_mentors)) {
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.Rounded.Edit,
                            contentDescription = context.getString(R.string.route_marking),
                            tint = if (currentRoute == context.getString(R.string.route_marking)) {
                                MaterialTheme.colorScheme.primary // Tint with primary color when selected
                            } else {
                                MaterialTheme.colorScheme.onSurface // Default tint color
                            }
                        )
                    }, // Add Edit icon
                    label = {
                        Text(
                            text = context.getString(R.string.route_marking),
                            style = MaterialTheme.typography.bodyMedium, // Set label typography
                            color = if (currentRoute == context.getString(R.string.route_marking)) {
                                MaterialTheme.colorScheme.primary // Highlight selected item
                            } else {
                                MaterialTheme.colorScheme.onSurface // Default label color
                            }
                        )
                    },
                    selected = currentRoute == context.getString(R.string.route_marking),
                    onClick = { onDestinationClicked(context.getString(R.string.route_marking)) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
            NavigationDrawerItem(
                icon = {
                    Icon(
                        Icons.Rounded.Info,
                        contentDescription = context.getString(R.string.route_about),
                        tint = if (currentRoute == context.getString(R.string.route_about)) {
                            MaterialTheme.colorScheme.primary // Tint with primary color when selected
                        } else {
                            MaterialTheme.colorScheme.onSurface // Default tint color
                        }
                    )
                }, // Add About icon
                label = {
                    Text(
                        text = context.getString(R.string.route_about),
                        style = MaterialTheme.typography.bodyMedium, // Set label typography
                        color = if (currentRoute == context.getString(R.string.route_about)) {
                            MaterialTheme.colorScheme.primary // Highlight selected item
                        } else {
                            MaterialTheme.colorScheme.onSurface // Default label color
                        }
                    )
                },
                selected = currentRoute == context.getString(R.string.route_about),
                onClick = { onDestinationClicked(context.getString(R.string.route_about)) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
            NavigationDrawerItem(
                icon = {
                    Icon(
                        Icons.Rounded.Feedback,
                        contentDescription = context.getString(R.string.route_feedback),
                        tint = if (currentRoute == context.getString(R.string.route_feedback)) {
                            MaterialTheme.colorScheme.primary // Tint with primary color when selected
                        } else {
                            MaterialTheme.colorScheme.onSurface // Default tint color
                        }
                    )
                }, // Add About icon
                label = {
                    Text(
                        text = context.getString(R.string.route_feedback),
                        style = MaterialTheme.typography.bodyMedium, // Set label typography
                        color = if (currentRoute == context.getString(R.string.route_feedback)) {
                            MaterialTheme.colorScheme.primary // Highlight selected item
                        } else {
                            MaterialTheme.colorScheme.onSurface // Default label color
                        }
                    )
                },
                selected = currentRoute == context.getString(R.string.route_feedback),
                onClick = { onDestinationClicked(context.getString(R.string.route_feedback)) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}