package com.bitflip.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bitflip.app.ui.screens.AboutScreen
import com.bitflip.app.ui.screens.ConverterScreen
import com.bitflip.app.ui.screens.ReferenceScreen
import com.bitflip.app.ui.theme.*

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Converter  : Screen("converter",  "Converter", Icons.Outlined.SwapHoriz)
    object Reference  : Screen("reference",  "Reference", Icons.AutoMirrored.Outlined.ListAlt)
    object About      : Screen("about",      "About",     Icons.Outlined.Info)
}

val bottomNavItems = listOf(Screen.Converter, Screen.Reference, Screen.About)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BitFlipApp()
        }
    }
}

@Composable
fun BitFlipThemeWrapper(content: @Composable () -> Unit) {
    com.bitflip.app.ui.theme.BitFlipTheme(content = content)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BitFlipApp() {
    BitFlipThemeWrapper {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDest = navBackStackEntry?.destination

        Scaffold(
            containerColor = BgPrimary,
            bottomBar = {
                NavigationBar(
                    containerColor = BgSurface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentDest?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    screen.icon,
                                    contentDescription = screen.label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(screen.label, fontSize = 11.sp)
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = AccentBlue,
                                selectedTextColor   = AccentBlue,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor      = AccentBlue.copy(alpha = 0.12f)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Converter.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(Screen.Converter.route) { ConverterScreen() }
                composable(Screen.Reference.route) { ReferenceScreen() }
                composable(Screen.About.route)     { AboutScreen() }
            }
        }
    }
}

