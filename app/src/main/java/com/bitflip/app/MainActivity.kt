package com.bitflip.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.bitflip.app.ui.screens.*
import com.bitflip.app.ui.theme.*
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard  : Screen("dashboard",  "Dashboard",   Icons.Outlined.Dashboard)
    object Converter  : Screen("converter",  "Converter",   Icons.Outlined.SwapHoriz)
    object Arithmetic : Screen("arithmetic", "Arithmetic",  Icons.Outlined.Calculate)
    object Reference  : Screen("reference",  "Reference",   Icons.AutoMirrored.Outlined.ListAlt)
    object Calculator : Screen("calculator", "Calculator",  Icons.Outlined.Functions)
    object History    : Screen("history",    "History",     Icons.Outlined.History)
    object About      : Screen("about",      "About",       Icons.Outlined.Info)
}

val allScreens = listOf(
    Screen.Dashboard,
    Screen.Calculator,
    Screen.Converter,
    Screen.Arithmetic,
    Screen.Reference,
    Screen.History,
    Screen.About
)

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
        var backStack by rememberSaveable { mutableStateOf(listOf(Screen.Dashboard.route)) }
        val selectedRoute = backStack.last()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        BackHandler(enabled = drawerState.isOpen || backStack.size > 1) {
            if (drawerState.isOpen) {
                scope.launch { drawerState.close() }
            } else if (backStack.size > 1) {
                backStack = backStack.dropLast(1)
            }
        }
        val currentScreen = allScreens.firstOrNull { it.route == selectedRoute } ?: Screen.Dashboard

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

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                BitFlipDrawerContent(
                    currentRoute = selectedRoute,
                    onNavigate = { route ->
                        if (backStack.last() != route) {
                            backStack = backStack + route
                        }
                        scope.launch { drawerState.close() }
                    }
                )
            },
            scrimColor = Color.Black.copy(alpha = 0.6f)
        ) {
            Scaffold(
                containerColor = BgPrimary,
                topBar = {
                    if (currentScreen != Screen.Dashboard) {
                        BitFlipTopBar(
                            title = currentScreen.label,
                            onMenuClick = { scope.launch { drawerState.open() } },
                            onBackClick = {
                                if (backStack.size > 1) {
                                    backStack = backStack.dropLast(1)
                                }
                            }
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 } togetherWith
                                    fadeOut(tween(200)) + slideOutHorizontally(tween(200)) { -it / 4 }
                        },
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            Screen.Dashboard -> DashboardScreen(
                                onNavigateToTool = { route ->
                                    if (backStack.last() != route) {
                                        backStack = backStack + route
                                    }
                                },
                                onMenuClick = { scope.launch { drawerState.open() } }
                            )
                            Screen.Converter  -> ConverterScreen()
                            Screen.Arithmetic -> ArithmeticScreen()
                            Screen.Reference  -> ReferenceScreen()
                            Screen.History    -> HistoryScreen()
                            Screen.About      -> AboutScreen()
                            Screen.Calculator -> ScientificCalculatorScreen()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BitFlipTopBar(
    title: String,
    onMenuClick: () -> Unit,
    onBackClick: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BgSurface,
            titleContentColor = TextPrimary,
            navigationIconContentColor = TextPrimary,
            actionIconContentColor = TextMuted
        ),
        title = {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(22.dp)
                )
            }
        },
        actions = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    Icons.Outlined.Menu,
                    contentDescription = "Menu",
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    )
}

@Composable
fun BitFlipDrawerContent(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = BgSurface,
        drawerContentColor = TextPrimary,
        modifier = Modifier.width(300.dp),
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            // Drawer header with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                AccentBlue.copy(alpha = 0.12f),
                                AccentPurple.copy(alpha = 0.06f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(horizontal = 24.dp)
                    .padding(top = 48.dp, bottom = 24.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_icon),
                            contentDescription = "BitFlip Logo",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 1.5.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(AccentBlue, AccentPurple)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        )
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "BitFlip",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary,
                                letterSpacing = (-0.5).sp
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AccentBlue.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "v${BuildConfig.VERSION_NAME}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentBlue,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Number System Toolkit",
                        fontSize = 12.sp,
                        color = TextMuted,
                        letterSpacing = 0.3.sp
                    )
                }
            }

            HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

            Spacer(Modifier.height(8.dp))

            // Navigation section label
            Text(
                text = "NAVIGATION",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            // Nav items
            allScreens.forEach { screen ->
                DrawerNavItem(
                    screen = screen,
                    isSelected = currentRoute == screen.route,
                    onClick = { onNavigate(screen.route) }
                )
            }

            Spacer(Modifier.weight(1f))

            // Footer section
            HorizontalDivider(
                color = BorderColor,
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(12.dp))

            // Developer credit
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            AccentPurple.copy(alpha = 0.12f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = null,
                        tint = AccentPurple,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Saad Khan",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "© 2026 All Rights Reserved",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun DrawerNavItem(
    screen: Screen,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) AccentBlue.copy(alpha = 0.1f) else Color.Transparent
    val contentColor = if (isSelected) AccentBlue else TextMuted
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    if (isSelected) AccentBlue.copy(alpha = 0.15f)
                    else BgSurface2,
                    RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = screen.icon,
                contentDescription = screen.label,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = screen.label,
            fontSize = 14.sp,
            fontWeight = fontWeight,
            color = if (isSelected) TextPrimary else TextMuted,
            letterSpacing = 0.2.sp
        )

        if (isSelected) {
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(20.dp)
                    .background(AccentBlue, RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
fun UpdateDialog(update: AppUpdate, onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    AlertDialog(
        modifier = Modifier.glassCard(20),
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false),
        containerColor = Color.Transparent,
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
