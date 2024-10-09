package com.arham.uhack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.arham.uhack.ui.navigation.NavigationHost
import com.arham.uhack.ui.theme.UHackTheme
import kotlinx.coroutines.launch

class NavigationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UHackTheme {
                val view = LocalView.current
                if (!view.isInEditMode) {
                    val window = (view.context as android.app.Activity).window
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                }
                NavigationScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationScreen() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    var currentView by remember { mutableStateOf("Home") }
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
                }
            )
        },
        content = {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(currentView) },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Filled.Menu, contentDescription = "Menu")
                            }
                        }
                    )
                }
            ) { paddingValues ->
                NavigationHost(
                    navController = navController,
                    paddingValues = paddingValues
                )
            }
        }
    )
}

@Composable
fun DrawerContent(onDestinationClicked: (route: String) -> Unit) {
    ModalDrawerSheet {
        Column {
            Text(text = "Hack-Nav", modifier = Modifier.padding(16.dp))
            HorizontalDivider()
            NavigationDrawerItem(
                label = { Text("Home") },
                selected = false,
                onClick = { onDestinationClicked("Home") },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
            NavigationDrawerItem(
                label = { Text("Marking") },
                selected = false,
                onClick = { onDestinationClicked("Marking") },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationScreenPreview() {
    UHackTheme {
        NavigationScreen()
    }
}