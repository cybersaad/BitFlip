package com.numberconverter.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numberconverter.app.ui.theme.*

data class RefRow(val dec: Int, val bin: String, val oct: String, val hex: String)

private val TABLE_DATA = (0..15).map { n ->
    RefRow(
        dec = n,
        bin = n.toString(2).padStart(4, '0'),
        oct = n.toString(8),
        hex = n.toString(16).uppercase()
    )
}

@Composable
fun ReferenceScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgPrimary)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text("Reference", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = AccentGreen, letterSpacing = (-0.5).sp)
        Text("Quick lookup 0 – 15", fontSize = 13.sp, color = TextMuted, modifier = Modifier.padding(top = 2.dp, bottom = 20.dp))

        // Table
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = BgSurface,
            border = BorderStroke(0.5.dp, BorderColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                // Header row
                TableRow(
                    dec = "DEC", bin = "BIN", oct = "OCT", hex = "HEX",
                    isHeader = true
                )
                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                TABLE_DATA.forEachIndexed { idx, row ->
                    TableRow(
                        dec = row.dec.toString(),
                        bin = row.bin,
                        oct = row.oct,
                        hex = row.hex,
                        isHeader = false,
                        isAlt = idx % 2 == 1
                    )
                    if (idx < TABLE_DATA.lastIndex) {
                        HorizontalDivider(color = BorderColor.copy(alpha = 0.4f), thickness = 0.5.dp)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Formula cards
        Text("Conversion Methods", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextMuted, letterSpacing = 0.5.sp)
        Spacer(Modifier.height(10.dp))

        FormulaCard(
            title = "Any base → Decimal",
            body = "Multiply each digit by its base raised to its position power, then sum all values.\n\nEx: BIN 1101 = 1×2³ + 1×2² + 0×2¹ + 1×2⁰ = 13",
            accent = AccentBlue
        )
        Spacer(Modifier.height(10.dp))
        FormulaCard(
            title = "Decimal → Any base",
            body = "Divide the number by the target base repeatedly. Collect remainders. Read remainders bottom-up.\n\nEx: 13 ÷ 2 → remainders 1,0,1,1 → read up = 1101",
            accent = AccentPurple
        )
        Spacer(Modifier.height(10.dp))
        FormulaCard(
            title = "Hexadecimal digits",
            body = "HEX uses 0–9 then A=10, B=11, C=12, D=13, E=14, F=15.\n\nEx: FF = 15×16 + 15 = 255",
            accent = AccentGreen
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun TableRow(dec: String, bin: String, oct: String, hex: String, isHeader: Boolean, isAlt: Boolean = false) {
    val bg = when {
        isHeader -> BgSurface2
        isAlt    -> BgSurface2.copy(alpha = 0.4f)
        else     -> BgSurface
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(dec to AccentBlue, bin to AccentPurple, oct to AccentPink, hex to AccentGreen)
            .forEachIndexed { i, (value, accent) ->
                Text(
                    value,
                    modifier = Modifier.weight(1f),
                    textAlign = if (i == 0) TextAlign.Start else TextAlign.Center,
                    fontSize = if (isHeader) 11.sp else 15.sp,
                    fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    color = if (isHeader) accent else if (i == 0) AccentBlue else TextPrimary,
                    letterSpacing = if (isHeader) 0.8.sp else 0.5.sp
                )
            }
    }
}

@Composable
fun FormulaCard(title: String, body: String, accent: androidx.compose.ui.graphics.Color) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = BgSurface,
        border = BorderStroke(0.5.dp, BorderColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .background(accent.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = accent)
            }
            Spacer(Modifier.height(8.dp))
            Text(body, fontSize = 13.sp, color = TextMuted, lineHeight = 20.sp, fontFamily = FontFamily.Monospace)
        }
    }
}
