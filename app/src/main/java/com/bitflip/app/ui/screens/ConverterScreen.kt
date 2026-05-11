package com.bitflip.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitflip.app.Base
import com.bitflip.app.ConversionEngine
import com.bitflip.app.ConversionResult
import com.bitflip.app.StepGroup
import com.bitflip.app.ui.theme.*

@Composable
fun ConverterScreen() {
    var selectedBase by remember { mutableStateOf(Base.DECIMAL) }
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<ConversionResult?>(null) }
    var steps by remember { mutableStateOf<List<StepGroup>>(emptyList()) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(input, selectedBase) {
        if (input.isEmpty()) {
            result = null; steps = emptyList(); isError = false
        } else {
            isError = !ConversionEngine.isValid(input, selectedBase)
            if (!isError) {
                result = ConversionEngine.convert(input, selectedBase)
                steps = ConversionEngine.buildSteps(input, selectedBase)
            } else {
                result = null
                steps = emptyList()
            }
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

        // Header
        Text(
            "BitFlip",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AccentBlue,
            letterSpacing = (-0.5).sp
        )
        Text(
            "Decimal · Binary · Octal · Hex",
            fontSize = 13.sp,
            color = TextMuted,
            modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
        )

        // Base selector tabs
        BaseSelector(selectedBase) { base ->
            selectedBase = base
            input = ""
            result = null
            steps = emptyList()
            isError = false
        }

        Spacer(Modifier.height(16.dp))

        // Input field
        InputCard(
            base = selectedBase,
            input = input,
            isError = isError,
            onInputChange = { input = it.uppercase() }
        )

        Spacer(Modifier.height(16.dp))

        // Result cards
        AnimatedVisibility(visible = result != null) {
            result?.let { res ->
                ResultsSection(fromBase = selectedBase, result = res)
            }
        }

        Spacer(Modifier.height(16.dp))

        AnimatedVisibility(visible = steps.isNotEmpty()) {
            Column {
                steps.forEach { group ->
                    StepGroupCard(group)
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun BaseSelector(selected: Base, onSelect: (Base) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Base.values().forEach { base ->
            val isSelected = base == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) AccentBlue else BgSurface)
                    .border(
                        width = if (isSelected) 0.dp else 0.5.dp,
                        color = if (isSelected) Color.Transparent else BorderColor,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelect(base) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    base.short,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) BgPrimary else TextMuted,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
fun InputCard(base: Base, input: String, isError: Boolean, onInputChange: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = BgSurface,
        border = BorderStroke(0.5.dp, BorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BgSurface2,
                    border = BorderStroke(0.5.dp, BorderColor)
                ) {
                    Text(
                        base.short,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentBlue,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(base.label, fontSize = 14.sp, color = TextMuted)
            }
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Enter ${base.label.lowercase()} number…",
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp
                    )
                },
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                ),
                isError = isError,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = when (base) {
                        Base.HEX -> KeyboardType.Text
                        Base.DECIMAL -> KeyboardType.Decimal
                        Base.BINARY -> KeyboardType.Decimal
                        Base.OCTAL -> KeyboardType.Decimal
                    },
                    capitalization = KeyboardCapitalization.Characters
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = BorderColor,
                    errorBorderColor = AccentRed,
                    focusedContainerColor = BgSurface2,
                    unfocusedContainerColor = BgSurface2,
                    errorContainerColor = BgSurface2,
                    cursorColor = AccentBlue
                ),
                shape = RoundedCornerShape(10.dp)
            )
            Spacer(Modifier.height(6.dp))
            if (isError) {
                Text("✕  Invalid input for ${base.label.lowercase()}", fontSize = 12.sp, color = AccentRed)
            } else {
                Text(base.hint, fontSize = 12.sp, color = TextMuted, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun ResultsSection(fromBase: Base, result: ConversionResult) {
    val cards = listOf(
        Triple(Base.DECIMAL, result.decimal, AccentBlue),
        Triple(Base.BINARY,  result.binary,  AccentPurple),
        Triple(Base.OCTAL,   result.octal,   AccentPink),
        Triple(Base.HEX,     result.hex,     AccentGreen),
    ).filter { it.first != fromBase }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        cards.forEach { (base, value, accent) ->
            ResultCard(base = base, value = value, accent = accent)
        }
    }
}

@Composable
fun ResultCard(base: Base, value: String, accent: Color) {
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            kotlinx.coroutines.delay(1500)
            copied = false
        }
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = BgSurface,
        border = BorderStroke(0.5.dp, BorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box {
            // Top accent line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(accent)
            )
            Row(
                modifier = Modifier.padding(top = 2.dp).padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        base.label.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = accent,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        value,
                        fontSize = if (value.length > 20) 16.sp else 24.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = TextPrimary,
                        letterSpacing = 1.sp,
                        lineHeight = 28.sp
                    )
                    Text(
                        "Base ${base.radix}",
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                FilledTonalIconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText(base.label, value))
                        copied = true
                    },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = if (copied) accent.copy(alpha = 0.15f) else BgSurface2,
                        contentColor = if (copied) accent else TextMuted
                    )
                ) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(18.dp))
                }
            }
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
