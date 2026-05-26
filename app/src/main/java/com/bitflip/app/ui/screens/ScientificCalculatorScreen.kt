package com.bitflip.app.ui.screens


import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.bitflip.app.CalculatorEngine
import com.bitflip.app.HistoryItem
import com.bitflip.app.HistoryManager
import com.bitflip.app.ui.theme.*

@Composable
fun ScientificCalculatorScreen() {
    val context = LocalContext.current
    var expression by remember { mutableStateOf("") }
    var displayResult by remember { mutableStateOf("") }
    var isRadians by remember { mutableStateOf(false) }
    var justEvaluated by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    // Real-time preview
    LaunchedEffect(expression, isRadians) {
        if (!justEvaluated && expression.isNotEmpty()) {
            val result = CalculatorEngine.evaluate(expression, useDegrees = !isRadians)
            if (result.isSuccess) {
                displayResult = CalculatorEngine.formatResult(result.getOrThrow())
                hasError = false
            } else {
                displayResult = ""
            }
        } else if (expression.isEmpty()) {
            displayResult = ""
            hasError = false
        }
    }

    val onButton: (String) -> Unit = onButton@{ label ->
        val isDigit = label.length == 1 && label[0].isDigit()
        val isOp = label in listOf("+", "-", "×", "÷")
        val isFunc = label in listOf("sin", "cos", "tan", "ln", "log", "√", "|x|")
        val isConst = label in listOf("π", "e")

        // Post-evaluation reset
        if (justEvaluated && label != "=" && label != "AC") {
            val wasError = hasError
            justEvaluated = false
            hasError = false
            when {
                isDigit || label == "." || isFunc || isConst || label == "(" -> {
                    expression = ""
                }
                isOp -> {
                    expression = if (wasError) "" else displayResult
                }
                else -> {
                    expression = if (wasError) "" else displayResult
                }
            }
        }

        when (label) {
            "AC" -> {
                expression = ""; displayResult = ""
                justEvaluated = false; hasError = false
            }
            "⌫" -> {
                if (expression.isNotEmpty()) {
                    expression = smartBackspace(expression)
                    justEvaluated = false
                }
            }
            "=" -> {
                if (expression.isNotEmpty()) {
                    val result = CalculatorEngine.evaluate(expression, useDegrees = !isRadians)
                    if (result.isSuccess) {
                        displayResult = CalculatorEngine.formatResult(result.getOrThrow())
                        justEvaluated = true; hasError = false
                        HistoryManager.addHistory(context, HistoryItem("Calculator", "$expression = $displayResult"))
                    } else {
                        displayResult = "Error"; hasError = true
                    }
                }
            }
            "±" -> {
                justEvaluated = false
                expression = if (expression.isEmpty()) "-"
                else if (expression.startsWith("-")) expression.drop(1)
                else "-$expression"
            }
            "DEG", "RAD" -> { isRadians = !isRadians; justEvaluated = false }
            "sin", "cos", "tan" -> { expression += "$label("; justEvaluated = false }
            "ln" -> { expression += "ln("; justEvaluated = false }
            "log" -> { expression += "log("; justEvaluated = false }
            "√" -> { expression += "√("; justEvaluated = false }
            "|x|" -> { expression += "abs("; justEvaluated = false }
            "x²" -> { expression += "²"; justEvaluated = false }
            "xⁿ" -> { expression += "^("; justEvaluated = false }
            "π" -> { expression += "π"; justEvaluated = false }
            "e" -> { expression += "e"; justEvaluated = false }
            "!" -> { expression += "!"; justEvaluated = false }
            "%" -> { expression += "%"; justEvaluated = false }
            "(" -> { expression += "("; justEvaluated = false }
            ")" -> { expression += ")"; justEvaluated = false }
            else -> { expression += label; justEvaluated = false }
        }
    }

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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    "Calculator",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AccentAmber,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    "Scientific",
                    fontSize = 13.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        // Display
        CalcDisplay(
            expression = expression,
            result = displayResult,
            isRadians = isRadians,
            isResult = justEvaluated,
            hasError = hasError,
            modifier = Modifier.weight(1f)
        )

        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)

        // Buttons
        CalcButtonGrid(isRadians = isRadians, onButton = onButton)
        }
    }
}

// ── Display ────────────────────────────────────────────────────

@Composable
private fun CalcDisplay(
    expression: String,
    result: String,
    isRadians: Boolean,
    isResult: Boolean,
    hasError: Boolean,
    modifier: Modifier = Modifier
) {
    val resultSize = if (isResult) 38f else 20f
    val exprSize = if (isResult) 16f else 28f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BgPrimary, BgSurface.copy(alpha = 0.3f))
                )
            )
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            // Mode badge
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isRadians) AccentPurple.copy(alpha = 0.12f)
                else AccentGreen.copy(alpha = 0.12f),
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text(
                    text = if (isRadians) "RAD" else "DEG",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRadians) AccentPurple else AccentGreen,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(Modifier.weight(1f))

            // Expression
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState(), reverseScrolling = true),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = expression.ifEmpty { "0" },
                    fontSize = exprSize.sp,
                    fontWeight = if (isResult) FontWeight.Normal else FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (isResult) TextMuted else TextPrimary,
                    maxLines = 1,
                    textAlign = TextAlign.End
                )
            }

            Spacer(Modifier.height(8.dp))

            // Result
            if (result.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState(), reverseScrolling = true),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (!isResult) {
                        Text(
                            text = "= ",
                            fontSize = resultSize.sp,
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Text(
                        text = result,
                        fontSize = resultSize.sp,
                        fontWeight = if (isResult) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = FontFamily.Monospace,
                        color = when {
                            hasError -> AccentRed
                            isResult -> AccentBlue
                            else -> TextMuted
                        },
                        maxLines = 1,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

// ── Button Grid ────────────────────────────────────────────────

@Composable
private fun CalcButtonGrid(isRadians: Boolean, onButton: (String) -> Unit) {
    val sciH = 38.dp
    val stdH = 52.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgSurface.copy(alpha = 0.3f))
            .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // ── Scientific rows ──
        // Row 1: DEG/RAD  sin  cos  tan  π
        Row(Modifier.fillMaxWidth().height(sciH), Arrangement.spacedBy(4.dp)) {
            CalcBtn(if (isRadians) "RAD" else "DEG", Modifier.weight(1f),
                AccentGreen.copy(0.15f), AccentGreen, 11.sp, FontWeight.Bold) {
                onButton(if (isRadians) "RAD" else "DEG")
            }
            CalcBtn("sin", Modifier.weight(1f), BgSurface2, AccentBlue, 13.sp) { onButton("sin") }
            CalcBtn("cos", Modifier.weight(1f), BgSurface2, AccentBlue, 13.sp) { onButton("cos") }
            CalcBtn("tan", Modifier.weight(1f), BgSurface2, AccentBlue, 13.sp) { onButton("tan") }
            CalcBtn("π", Modifier.weight(1f), BgSurface2, AccentPink, 16.sp) { onButton("π") }
        }

        // Row 2: x²  ln  log  √  e
        Row(Modifier.fillMaxWidth().height(sciH), Arrangement.spacedBy(4.dp)) {
            CalcBtn("x²", Modifier.weight(1f), BgSurface2, AccentPurple, 13.sp) { onButton("x²") }
            CalcBtn("ln", Modifier.weight(1f), BgSurface2, AccentGreen, 13.sp) { onButton("ln") }
            CalcBtn("log", Modifier.weight(1f), BgSurface2, AccentGreen, 12.sp) { onButton("log") }
            CalcBtn("√", Modifier.weight(1f), BgSurface2, AccentPurple, 16.sp) { onButton("√") }
            CalcBtn("e", Modifier.weight(1f), BgSurface2, AccentPink, 15.sp) { onButton("e") }
        }

        // Row 3: xⁿ  n!  (  )  |x|
        Row(Modifier.fillMaxWidth().height(sciH), Arrangement.spacedBy(4.dp)) {
            CalcBtn("xⁿ", Modifier.weight(1f), BgSurface2, AccentPurple, 13.sp) { onButton("xⁿ") }
            CalcBtn("n!", Modifier.weight(1f), BgSurface2, AccentPurple, 13.sp) { onButton("!") }
            CalcBtn("(", Modifier.weight(1f), BgSurface2, TextMuted, 16.sp) { onButton("(") }
            CalcBtn(")", Modifier.weight(1f), BgSurface2, TextMuted, 16.sp) { onButton(")") }
            CalcBtn("|x|", Modifier.weight(1f), BgSurface2, AccentGreen, 12.sp) { onButton("|x|") }
        }

        Spacer(Modifier.height(2.dp))

        // ── Standard rows ──
        // Row: AC  ⌫  %  ÷
        Row(Modifier.fillMaxWidth().height(stdH), Arrangement.spacedBy(4.dp)) {
            CalcBtn("AC", Modifier.weight(1f), AccentRed.copy(0.12f), AccentRed, 16.sp, FontWeight.Bold) { onButton("AC") }
            CalcBtn("⌫", Modifier.weight(1f), BgSurface2, AccentPink, 18.sp) { onButton("⌫") }
            CalcBtn("%", Modifier.weight(1f), BgSurface2, TextMuted, 18.sp) { onButton("%") }
            CalcBtn("÷", Modifier.weight(1f), AccentBlue.copy(0.12f), AccentBlue, 22.sp, FontWeight.Bold) { onButton("÷") }
        }

        // Row: 7  8  9  ×
        Row(Modifier.fillMaxWidth().height(stdH), Arrangement.spacedBy(4.dp)) {
            CalcBtn("7", Modifier.weight(1f), BgSurface, TextPrimary, 22.sp) { onButton("7") }
            CalcBtn("8", Modifier.weight(1f), BgSurface, TextPrimary, 22.sp) { onButton("8") }
            CalcBtn("9", Modifier.weight(1f), BgSurface, TextPrimary, 22.sp) { onButton("9") }
            CalcBtn("×", Modifier.weight(1f), AccentBlue.copy(0.12f), AccentBlue, 22.sp, FontWeight.Bold) { onButton("×") }
        }

        // Row: 4  5  6  -
        Row(Modifier.fillMaxWidth().height(stdH), Arrangement.spacedBy(4.dp)) {
            CalcBtn("4", Modifier.weight(1f), BgSurface, TextPrimary, 22.sp) { onButton("4") }
            CalcBtn("5", Modifier.weight(1f), BgSurface, TextPrimary, 22.sp) { onButton("5") }
            CalcBtn("6", Modifier.weight(1f), BgSurface, TextPrimary, 22.sp) { onButton("6") }
            CalcBtn("-", Modifier.weight(1f), AccentBlue.copy(0.12f), AccentBlue, 22.sp, FontWeight.Bold) { onButton("-") }
        }

        // Row: 1  2  3  +
        Row(Modifier.fillMaxWidth().height(stdH), Arrangement.spacedBy(4.dp)) {
            CalcBtn("1", Modifier.weight(1f), BgSurface, TextPrimary, 22.sp) { onButton("1") }
            CalcBtn("2", Modifier.weight(1f), BgSurface, TextPrimary, 22.sp) { onButton("2") }
            CalcBtn("3", Modifier.weight(1f), BgSurface, TextPrimary, 22.sp) { onButton("3") }
            CalcBtn("+", Modifier.weight(1f), AccentBlue.copy(0.12f), AccentBlue, 22.sp, FontWeight.Bold) { onButton("+") }
        }

        // Row: ±  0  .  =
        Row(Modifier.fillMaxWidth().height(stdH), Arrangement.spacedBy(4.dp)) {
            CalcBtn("±", Modifier.weight(1f), BgSurface2, TextMuted, 18.sp) { onButton("±") }
            CalcBtn("0", Modifier.weight(1f), BgSurface, TextPrimary, 22.sp) { onButton("0") }
            CalcBtn(".", Modifier.weight(1f), BgSurface, TextPrimary, 22.sp) { onButton(".") }
            // Gradient = button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(listOf(AccentBlue, AccentPurple)))
                    .clickable { onButton("=") },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "=",
                    color = BgPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}

// ── Reusable Button ────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalcBtn(
    label: String,
    modifier: Modifier = Modifier,
    bgColor: Color = BgSurface,
    textColor: Color = TextPrimary,
    fontSize: TextUnit = 18.sp,
    fontWeight: FontWeight = FontWeight.Medium,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .glassCard(12, alpha = if (bgColor == BgSurface) 0.3f else 0.6f, borderColor = BorderColor.copy(alpha = 0.5f))
            .background(if (bgColor != BgSurface && bgColor != BgSurface2) bgColor.copy(alpha = 0.2f) else Color.Transparent)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = label,
                color = textColor,
                fontSize = fontSize,
                fontWeight = fontWeight,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

// ── Helpers ────────────────────────────────────────────────────

private fun smartBackspace(expr: String): String {
    val suffixes = listOf("sin(", "cos(", "tan(", "asin(", "acos(", "atan(",
        "ln(", "log(", "abs(", "√(")
    for (suffix in suffixes) {
        if (expr.endsWith(suffix)) return expr.dropLast(suffix.length)
    }
    return expr.dropLast(1)
}
