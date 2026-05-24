package com.bitflip.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.bitflip.app.ui.screens.AboutScreen
import com.bitflip.app.ui.screens.ArithmeticScreen
import com.bitflip.app.ui.screens.ConverterScreen
import com.bitflip.app.ui.screens.ReferenceScreen
import com.bitflip.app.ui.theme.*

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Converter  : Screen("converter",  "Converter", Icons.Outlined.SwapHoriz)
    object Arithmetic : Screen("arithmetic", "Arithmetic", Icons.Outlined.Calculate)
    object Reference  : Screen("reference",  "Reference", Icons.AutoMirrored.Outlined.ListAlt)
    object About      : Screen("about",      "About",     Icons.Outlined.Info)
}

val bottomNavItems = listOf(Screen.Converter, Screen.Arithmetic, Screen.Reference, Screen.About)

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
        var selectedRoute by rememberSaveable { mutableStateOf(Screen.Converter.route) }
        val currentScreen = bottomNavItems.firstOrNull { it.route == selectedRoute } ?: Screen.Converter

        // --- Update checker ---
        var updateInfo by remember { mutableStateOf<AppUpdate?>(null) }

        LaunchedEffect(Unit) {
            val currentVersion = BuildConfig.VERSION_NAME
            updateInfo = UpdateChecker.checkForUpdate(currentVersion)
        }

        if (updateInfo != null) {
            UpdateDialog(
                update = updateInfo!!,
                onDismiss = { updateInfo = null }
            )
        }
        // --- End update checker ---

        Scaffold(
            containerColor = BgPrimary,
            bottomBar = {
                NavigationBar(
                    containerColor = BgSurface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentScreen.route == screen.route
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
                                selectedRoute = screen.route
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentScreen) {
                    Screen.Converter  -> ConverterScreen()
                    Screen.Arithmetic -> ArithmeticScreen()
                    Screen.Reference  -> ReferenceScreen()
                    Screen.About      -> AboutScreen()
                }
            }
        }
    }
}

@Composable
fun UpdateDialog(update: AppUpdate, onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false),
        containerColor = BgSurface,
        shape = RoundedCornerShape(20.dp),
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(AccentBlue.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.SystemUpdate,
                    contentDescription = "Update",
                    tint = AccentBlue,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                "Update Available",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Version badge
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(BgSurface2, RoundedCornerShape(8.dp))
                            .border(0.5.dp, BorderColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "v${BuildConfig.VERSION_NAME}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    }
                    Text("→", fontSize = 16.sp, color = TextMuted)
                    Box(
                        modifier = Modifier
                            .background(AccentGreen.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "v${update.latestVersion}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentGreen
                        )
                    }
                }

                Text(
                    "A new version of BitFlip is available with improvements and new features.",
                    fontSize = 14.sp,
                    color = TextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    uriHandler.openUri(update.releaseUrl)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Update Now",
                    color = BgPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Later",
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }
        }
    )
}
