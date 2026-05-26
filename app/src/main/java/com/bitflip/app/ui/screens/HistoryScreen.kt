package com.bitflip.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitflip.app.HistoryItem
import com.bitflip.app.HistoryManager
import com.bitflip.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen() {
    val context = LocalContext.current
    var history by remember { mutableStateOf(HistoryManager.getHistory(context)) }

    val calcCount = history.count { it.tool == "Calculator" }
    val convCount = history.count { it.tool == "Converter" }
    val arithCount = history.count { it.tool == "Arithmetic" }

    val categories = listOf("Calculator", "Converter", "Arithmetic")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = 600.dp)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "History",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AccentBlue,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        "Recent Activity",
                        fontSize = 13.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                if (history.isNotEmpty()) {
                    IconButton(onClick = {
                        HistoryManager.clearHistory(context)
                        history = emptyList()
                    }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Clear History", tint = AccentRed)
                    }
                }
            }

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(modifier = Modifier.weight(1f), title = "Calculator", count = calcCount, color = AccentAmber)
                StatCard(modifier = Modifier.weight(1f), title = "Converter", count = convCount, color = AccentBlue)
                StatCard(modifier = Modifier.weight(1f), title = "Arithmetic", count = arithCount, color = AccentGreen)
            }

            if (history.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No history yet.", color = TextMuted, fontSize = 16.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    categories.forEach { category ->
                        val itemsInCategory = history.filter { it.tool == category }
                        if (itemsInCategory.isNotEmpty()) {
                            item {
                                Text(
                                    category.uppercase(),
                                    color = when(category) {
                                        "Calculator" -> AccentAmber
                                        "Converter" -> AccentBlue
                                        "Arithmetic" -> AccentGreen
                                        else -> TextMuted
                                    },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                                )
                            }
                            items(itemsInCategory) { item ->
                                HistoryCard(item)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(modifier: Modifier, title: String, count: Int, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = modifier
            .glassCard(12)
            .padding(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                count.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                title,
                fontSize = 10.sp,
                color = TextMuted
            )
        }
    }
}

@Composable
fun HistoryCard(item: HistoryItem) {
    val formatter = remember { SimpleDateFormat("MMM dd, HH:mm:ss", Locale.getDefault()) }
    val timeString = formatter.format(Date(item.timestamp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard(16)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = item.description,
                color = TextPrimary,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = timeString,
                color = TextMuted.copy(alpha = 0.7f),
                fontSize = 11.sp
            )
        }
    }
}
