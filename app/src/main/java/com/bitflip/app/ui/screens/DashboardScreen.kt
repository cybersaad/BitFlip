package com.bitflip.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitflip.app.BuildConfig
import com.bitflip.app.R
import com.bitflip.app.ui.theme.*
import kotlinx.coroutines.delay

data class ToolCardData(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color,
    val gradientEnd: Color,
    val route: String
)

val toolCards = listOf(
    ToolCardData(
        title = "Calculator",
        subtitle = "Scientific",
        description = "Full scientific calculator with trigonometry, logarithms, powers, factorials, and real-time evaluation.",
        icon = Icons.Outlined.Functions,
        accentColor = AccentAmber,
        gradientEnd = AccentRed,
        route = "calculator"
    ),
    ToolCardData(
        title = "Converter",
        subtitle = "Base Conversion",
        description = "Convert numbers between Decimal, Binary, Octal, and Hexadecimal with step-by-step explanations.",
        icon = Icons.Outlined.SwapHoriz,
        accentColor = AccentBlue,
        gradientEnd = AccentPurple,
        route = "converter"
    ),
    ToolCardData(
        title = "Arithmetic",
        subtitle = "Binary Operations",
        description = "Perform addition, subtraction, multiplication, and division on binary numbers with detailed steps.",
        icon = Icons.Outlined.Calculate,
        accentColor = AccentGreen,
        gradientEnd = AccentBlue,
        route = "arithmetic"
    ),
    ToolCardData(
        title = "Reference",
        subtitle = "Quick Lookup",
        description = "Instant reference table for values 0–15 across Decimal, Binary, Octal, and Hexadecimal systems.",
        icon = Icons.AutoMirrored.Outlined.ListAlt,
        accentColor = AccentPurple,
        gradientEnd = AccentPink,
        route = "reference"
    ),
    ToolCardData(
        title = "History",
        subtitle = "Activity Log",
        description = "View your recent calculations and conversions.",
        icon = Icons.Outlined.History,
        accentColor = AccentBlue,
        gradientEnd = AccentGreen,
        route = "history"
    ),
    ToolCardData(
        title = "About",
        subtitle = "App Info",
        description = "Developer information, version details, and links to project resources.",
        icon = Icons.Outlined.Info,
        accentColor = AccentPink,
        gradientEnd = AccentRed,
        route = "about"
    )
)



@Composable
fun DashboardScreen(onNavigateToTool: (String) -> Unit, onMenuClick: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 600.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Header
            item {
                DashboardHeader(onMenuClick = onMenuClick)
            }

            val calculators = toolCards.filter { it.route in listOf("calculator", "converter", "arithmetic") }
            val utilities = toolCards.filter { it.route in listOf("reference", "history") }
            val appInfo = toolCards.filter { it.route == "about" }

            // Calculators Section Title
            item {
                Text(
                    text = "Calculators",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 0.3.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            // Calculators Cards
            items(calculators) { tool ->
                ToolCard(tool = tool, onClick = { onNavigateToTool(tool.route) })
            }

            // Utilities Section Title
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Utilities & Resources",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 0.3.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            // Utilities Cards
            items(utilities) { tool ->
                ToolCard(tool = tool, onClick = { onNavigateToTool(tool.route) })
            }

            // App Info Section Title
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "App Info",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 0.3.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            // App Info Cards
            items(appInfo) { tool ->
                ToolCard(tool = tool, onClick = { onNavigateToTool(tool.route) })
            }

            // Footer
            item {
                Text(
                    text = "Made with ♥ by Saad Khan",
                    fontSize = 12.sp,
                    color = TextMuted.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun DashboardHeader(onMenuClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AccentBlue.copy(alpha = 0.08f),
                        Color.Transparent
                    )
                )
            )
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp, bottom = 4.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_icon),
                    contentDescription = "BitFlip Logo",
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(colors = listOf(AccentBlue, AccentPurple)),
                            shape = RoundedCornerShape(14.dp)
                        )
                )

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "BitFlip",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Number System Toolkit",
                        fontSize = 13.sp,
                        color = TextMuted,
                        letterSpacing = 0.3.sp
                    )
                }

                IconButton(onClick = onMenuClick) {
                    Icon(
                        Icons.Outlined.Menu,
                        contentDescription = "Menu",
                        tint = TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tagline glassy card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .glassCard(cornerRadius = 16)
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        AccentBlue.copy(alpha = 0.2f),
                                        AccentPurple.copy(alpha = 0.2f)
                                    )
                                ),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⚡", fontSize = 18.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Convert · Calculate · Learn",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Binary, Decimal, Octal & Hexadecimal",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ToolCard(tool: ToolCardData, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .glassCard(cornerRadius = 16)
            .clickable(onClick = onClick)
    ) {
        Column {
            // Top gradient accent strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(Brush.horizontalGradient(listOf(tool.accentColor, tool.gradientEnd)))
            )

            Row(
                modifier = Modifier
                    .padding(top = 3.dp)
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Icon container
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(tool.accentColor.copy(0.15f), tool.gradientEnd.copy(0.08f))
                            ),
                            RoundedCornerShape(14.dp)
                        )
                        .border(0.5.dp, tool.accentColor.copy(0.3f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(tool.icon, contentDescription = tool.title, tint = tool.accentColor, modifier = Modifier.size(24.dp))
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(tool.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Surface(shape = RoundedCornerShape(6.dp), color = tool.accentColor.copy(alpha = 0.12f)) {
                            Text(
                                text = tool.subtitle,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = tool.accentColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                letterSpacing = 0.3.sp
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(tool.description, fontSize = 12.sp, color = TextMuted, lineHeight = 17.sp)
                }

                Spacer(Modifier.width(8.dp))

                // Arrow indicator
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(28.dp)
                        .background(BgSurface2.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .border(0.5.dp, BorderColor, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("›", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                }
            }
        }
    }
}
