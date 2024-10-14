package com.arham.uhack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.arham.uhack.data.FirestoreSyncManager
import com.arham.uhack.data.QRCode
import com.arham.uhack.ui.navigation.NavigationHost
import com.arham.uhack.ui.theme.UHackTheme
import kotlinx.coroutines.launch
import com.arham.uhack.ui.navigation.DrawerContent

class NavigationActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            UHackTheme {
                NavigationScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScreen() {
    val context = LocalContext.current
    QRCode.updateColour(context, MaterialTheme.colorScheme.primary.toArgb(), MaterialTheme.colorScheme.surfaceVariant.toArgb())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val firestoreSyncManager = FirestoreSyncManager(context)
    var currentView by remember { mutableStateOf(context.getString(R.string.route_home)) }
    var currentStack by remember { mutableStateOf<String?>(null) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onDestinationClicked = { route ->
                    // Handle navigation based on the route
                    if (currentStack == null) {
                        currentStack = route // Set initial destination for the stack
                        navController.navigate(route) { // Modify navigate function
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                        }
                    }
                    else {
                        navController.navigate(route) {popUpTo(currentStack!!) {
                            inclusive = true } // Pop to current stack start
                            currentStack = route // Update current stack start
                        }
                    }
                    currentView = route
                    scope.launch { drawerState.close() }
                },
                currentView,
                firestoreSyncManager
            )
        },
        content = {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(currentView, style = MaterialTheme.typography.titleLarge) },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Rounded.Menu, contentDescription = context.getString(R.string.description_menu), tint = MaterialTheme.colorScheme.onSurface)
                            }
                        },
                        actions = {
                            if (firestoreSyncManager.photoUrl != "null") {
                                AsyncImage(
                                    model = firestoreSyncManager.photoUrl,
                                    contentDescription = context.getString(R.string.description_profile_image),
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.AccountCircle,
                                    contentDescription = context.getString(R.string.description_profile_image),
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                            actionIconContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            ) { paddingValues ->
                NavigationHost(
                    navController = navController,
                    paddingValues = paddingValues,
                    firestoreSyncManager = firestoreSyncManager
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun NavigationScreenPreview() {
    UHackTheme {
        NavigationScreen()
    }
}