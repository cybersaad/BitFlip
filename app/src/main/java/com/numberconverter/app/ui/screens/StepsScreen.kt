package com.numberconverter.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.TextStyle
import com.numberconverter.app.Base
import com.numberconverter.app.ConversionEngine
import com.numberconverter.app.StepGroup
import com.numberconverter.app.ui.theme.*

@Composable
fun StepsScreen() {
    var selectedBase by remember { mutableStateOf(Base.DECIMAL) }
    var input by remember { mutableStateOf("") }
    var steps by remember { mutableStateOf<List<StepGroup>>(emptyList()) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(input, selectedBase) {
        if (input.isEmpty()) { steps = emptyList(); isError = false }
        else {
            isError = !ConversionEngine.isValid(input, selectedBase)
            steps = if (!isError) ConversionEngine.buildSteps(input, selectedBase) else emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text("Step-by-Step", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = AccentPurple, letterSpacing = (-0.5).sp)
        Text("See the full working", fontSize = 13.sp, color = TextMuted, modifier = Modifier.padding(top = 2.dp, bottom = 16.dp))

        // Base selector
        BaseSelector(selectedBase) { base ->
            selectedBase = base; input = ""; steps = emptyList(); isError = false
        }

        Spacer(Modifier.height(16.dp))

        // Input
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = BgSurface,
            border = BorderStroke(0.5.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it.uppercase() },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Enter ${selectedBase.label.lowercase()} number", color = TextMuted, fontSize = 13.sp) },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    ),
                    isError = isError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (selectedBase == Base.HEX) KeyboardType.Text else KeyboardType.Number,
                        capitalization = KeyboardCapitalization.Characters
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentPurple,
                        unfocusedBorderColor = BorderColor,
                        errorBorderColor = AccentRed,
                        focusedContainerColor = BgSurface2,
                        unfocusedContainerColor = BgSurface2,
                        errorContainerColor = BgSurface2,
                        cursorColor = AccentPurple
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                if (isError) {
                    Text("✕  Invalid input for ${selectedBase.label.lowercase()}", fontSize = 12.sp, color = AccentRed, modifier = Modifier.padding(top = 6.dp))
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (steps.isEmpty() && input.isNotEmpty() && !isError) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AccentPurple, modifier = Modifier.size(28.dp))
            }
        }

        steps.forEach { group ->
            StepGroupCard(group)
            Spacer(Modifier.height(12.dp))
        }

        if (steps.isEmpty() && input.isEmpty()) {
            EmptyState()
        }
    }
}

@Composable
fun StepGroupCard(group: StepGroup) {
    var expanded by remember(group.title) { mutableStateOf(true) }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = BgSurface,
        border = BorderStroke(0.5.dp, BorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    group.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentPurple,
                    letterSpacing = 0.3.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BgSurface2)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    group.lines.forEach { line ->
                        Text(
                            line.text,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (line.isResult) FontWeight.Bold else FontWeight.Normal,
                            color = if (line.isResult) AccentGreen else TextMuted,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🔢", fontSize = 40.sp)
            Spacer(Modifier.height(12.dp))
            Text("Enter a number above", fontSize = 14.sp, color = TextMuted)
            Text("to see step-by-step working", fontSize = 13.sp, color = TextMuted.copy(alpha = 0.6f))
        }
    }
}
