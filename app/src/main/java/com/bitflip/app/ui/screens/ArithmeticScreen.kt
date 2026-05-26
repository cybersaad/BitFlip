package com.bitflip.app.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitflip.app.StepGroup
import com.bitflip.app.StepLine
import com.bitflip.app.HistoryItem
import com.bitflip.app.HistoryManager
import com.bitflip.app.ui.theme.AccentBlue
import com.bitflip.app.ui.theme.AccentGreen
import com.bitflip.app.ui.theme.AccentPink
import com.bitflip.app.ui.theme.AccentPurple
import com.bitflip.app.ui.theme.AccentRed
import com.bitflip.app.ui.theme.BgPrimary
import com.bitflip.app.ui.theme.BgSurface
import com.bitflip.app.ui.theme.BgSurface2
import com.bitflip.app.ui.theme.BorderColor
import com.bitflip.app.ui.theme.TextMuted
import com.bitflip.app.ui.theme.TextPrimary
import com.bitflip.app.ui.theme.glassCard
import java.math.BigInteger

enum class BinaryOp(val label: String, val symbol: String, val accent: Color) {
    ADD("Addition", "+", AccentBlue),
    SUB("Subtraction", "-", AccentPurple),
    MUL("Multiplication", "x", AccentPink),
    DIV("Division", "/", AccentGreen)
}

data class BinaryOpResult(
    val value: String,
    val remainder: String? = null
)

private val binaryRegex = Regex("^[01]+$")

@Composable
fun ArithmeticScreen() {
    var inputA by remember { mutableStateOf("") }
    var inputB by remember { mutableStateOf("") }
    var selectedOp by remember { mutableStateOf(BinaryOp.ADD) }

    val aValid = inputA.isNotEmpty() && binaryRegex.matches(inputA)
    val bValid = inputB.isNotEmpty() && binaryRegex.matches(inputB)
    val bZero = bValid && inputB.all { it == '0' }

    val canCompute = aValid && bValid && !(selectedOp == BinaryOp.DIV && bZero)
    val result = if (canCompute) computeBinaryOp(inputA, inputB, selectedOp) else null
    val steps = if (result != null) buildArithmeticSteps(inputA, inputB, selectedOp, result) else emptyList()

    val context = LocalContext.current
    LaunchedEffect(inputA, inputB, selectedOp) {
        if (canCompute && result != null) {
            kotlinx.coroutines.delay(1500)
            val desc = "$inputA ${selectedOp.symbol} $inputB = ${result.value}" + 
                if (result.remainder != null) " R ${result.remainder}" else ""
            HistoryManager.addHistory(context, HistoryItem("Arithmetic", desc))
        }
    }

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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text(
                        "Arithmetic",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AccentGreen,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        "Binary operations",
                        fontSize = 13.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
                    )
                }
            }

            item {
                BinaryOpSelector(selectedOp) { selectedOp = it }
            }

            item {
                BinaryInputCard(
                    label = "A (binary)",
                    value = inputA,
                    isError = inputA.isNotEmpty() && !aValid,
                    onValueChange = { inputA = it }
                )
            }

            item {
                BinaryInputCard(
                    label = "B (binary)",
                    value = inputB,
                    isError = inputB.isNotEmpty() && !bValid,
                    onValueChange = { inputB = it }
                )
                if (selectedOp == BinaryOp.DIV && bZero) {
                    Text(
                        "Division by zero is not allowed.",
                        fontSize = 12.sp,
                        color = AccentRed,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            if (result != null) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        BinaryResultCard(
                            title = "Result",
                            value = result.value,
                            accent = selectedOp.accent
                        )
                        if (selectedOp == BinaryOp.DIV) {
                            BinaryResultCard(
                                title = "Remainder",
                                value = result.remainder ?: "0",
                                accent = AccentBlue
                            )
                        }
                    }
                }
            }

            if (steps.isNotEmpty()) {
                items(steps) { group ->
                    StepGroupCard(group)
                }
            }
        }
    }
}

@Composable
fun BinaryOpSelector(selected: BinaryOp, onSelect: (BinaryOp) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BinaryOp.entries.forEach { op ->
            val isSelected = op == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .glassCard(cornerRadius = 10, alpha = if (isSelected) 0.8f else 0.4f, borderColor = if (isSelected) op.accent else BorderColor)
                    .background(if (isSelected) op.accent else Color.Transparent)
                    .clickable { onSelect(op) },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        op.symbol,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) BgPrimary else TextMuted
                    )
                    Text(
                        op.label,
                        fontSize = 10.sp,
                        color = if (isSelected) BgPrimary else TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun BinaryInputCard(
    label: String,
    value: String,
    isError: Boolean,
    onValueChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard(16)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, fontSize = 13.sp, color = TextMuted)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = value,
                onValueChange = { raw ->
                    val filtered = raw.filter { it == '0' || it == '1' }
                    onValueChange(filtered)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Enter binary digits (0 or 1)",
                        color = TextMuted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp
                    )
                },
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                ),
                isError = isError,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
                Text("Invalid binary digits.", fontSize = 12.sp, color = AccentRed)
            } else {
                Text("Only 0 and 1 allowed.", fontSize = 12.sp, color = TextMuted)
            }
        }
    }
}

@Composable
fun BinaryResultCard(title: String, value: String, accent: Color) {
    val context = LocalContext.current
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            kotlinx.coroutines.delay(1500)
            copied = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .glassCard(16)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title.uppercase(),
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
                    letterSpacing = 1.sp
                )
            }
            Spacer(Modifier.width(12.dp))
            FilledTonalIconButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText(title, value))
                    copied = true
                },
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = if (copied) accent.copy(alpha = 0.15f) else BgSurface2,
                    contentColor = if (copied) accent else TextMuted
                )
            ) {
                Icon(
                    Icons.Outlined.ContentCopy,
                    contentDescription = "Copy",
                    modifier = Modifier.width(18.dp).height(18.dp)
                )
            }
        }
    }
}

private fun computeBinaryOp(aStr: String, bStr: String, op: BinaryOp): BinaryOpResult {
    val a = BigInteger(aStr, 2)
    val b = BigInteger(bStr, 2)

    return when (op) {
        BinaryOp.ADD -> BinaryOpResult(toBinarySigned(a + b))
        BinaryOp.SUB -> BinaryOpResult(toBinarySigned(a - b))
        BinaryOp.MUL -> BinaryOpResult(toBinarySigned(a * b))
        BinaryOp.DIV -> BinaryOpResult(
            toBinarySigned(a.divide(b)),
            toBinarySigned(a.remainder(b))
        )
    }
}

private fun toBinarySigned(value: BigInteger): String {
    return if (value.signum() < 0) {
        "-" + value.abs().toString(2)
    } else {
        value.toString(2)
    }
}

private fun buildArithmeticSteps(
    aStr: String,
    bStr: String,
    op: BinaryOp,
    result: BinaryOpResult
): List<StepGroup> {
    val group = when (op) {
        BinaryOp.ADD -> buildAdditionSteps(aStr, bStr, result.value)
        BinaryOp.SUB -> buildSubtractionSteps(aStr, bStr, result.value)
        BinaryOp.MUL -> buildMultiplicationSteps(aStr, bStr, result.value)
        BinaryOp.DIV -> buildDivisionSteps(aStr, bStr, result)
    }
    return listOf(group)
}

private fun buildAdditionSteps(aStr: String, bStr: String, result: String): StepGroup {
    val a = normalizeBinary(aStr)
    val b = normalizeBinary(bStr)
    val maxLen = maxOf(a.length, b.length)
    val aPad = a.padStart(maxLen, '0')
    val bPad = b.padStart(maxLen, '0')
    val lines = mutableListOf<StepLine>()

    lines += StepLine("A = $aPad")
    lines += StepLine("B = $bPad")
    lines += StepLine("Work from right to left, carry as needed.")

    var carry = 0
    for (i in maxLen - 1 downTo 0) {
        val bitA = aPad[i] - '0'
        val bitB = bPad[i] - '0'
        val sum = bitA + bitB + carry
        val resBit = sum % 2
        val newCarry = sum / 2
        val pos = maxLen - 1 - i
        lines += StepLine("bit $pos: $bitA + $bitB + carry $carry = $resBit, carry $newCarry")
        carry = newCarry
    }
    if (carry > 0) {
        lines += StepLine("final carry $carry added to the front")
    }

    lines += StepLine("Result = ${normalizeBinary(result)}", isResult = true)
    return StepGroup("Steps - Addition", lines)
}

private fun buildSubtractionSteps(aStr: String, bStr: String, result: String): StepGroup {
    val aVal = BigInteger(aStr, 2)
    val bVal = BigInteger(bStr, 2)
    val negative = aVal < bVal
    val left = if (negative) bStr else aStr
    val right = if (negative) aStr else bStr

    val a = normalizeBinary(left)
    val b = normalizeBinary(right)
    val maxLen = maxOf(a.length, b.length)
    val aPad = a.padStart(maxLen, '0')
    val bPad = b.padStart(maxLen, '0')
    val lines = mutableListOf<StepLine>()

    lines += StepLine("Inputs: A = ${normalizeBinary(aStr)}, B = ${normalizeBinary(bStr)}")
    if (negative) {
        lines += StepLine("Since A < B, compute B - A then prefix '-'")
    }
    lines += StepLine("Use A = $aPad")
    lines += StepLine("Use B = $bPad")
    lines += StepLine("Work from right to left, borrow as needed.")

    var borrow = 0
    for (i in maxLen - 1 downTo 0) {
        val bitA = aPad[i] - '0'
        val bitB = bPad[i] - '0'
        var current = bitA - borrow
        var newBorrow = 0
        if (current < bitB) {
            current += 2
            newBorrow = 1
        }
        val resBit = current - bitB
        val pos = maxLen - 1 - i
        lines += StepLine("bit $pos: ($bitA - borrow $borrow) - $bitB = $resBit, borrow $newBorrow")
        borrow = newBorrow
    }

    lines += StepLine("Result = $result", isResult = true)
    return StepGroup("Steps - Subtraction", lines)
}

private fun buildMultiplicationSteps(aStr: String, bStr: String, result: String): StepGroup {
    val a = normalizeBinary(aStr)
    val b = normalizeBinary(bStr)
    val lines = mutableListOf<StepLine>()

    lines += StepLine("A = $a")
    lines += StepLine("B = $b")
    lines += StepLine("Partial products (from right to left in B):")

    val reversed = b.reversed()
    for (i in reversed.indices) {
        val bit = reversed[i]
        if (bit == '1') {
            val partial = a + "0".repeat(i)
            lines += StepLine("bit $i = 1 -> A << $i = $partial")
        } else {
            lines += StepLine("bit $i = 0 -> partial = 0")
        }
    }

    lines += StepLine("Sum partials = ${normalizeBinary(result)}", isResult = true)
    return StepGroup("Steps - Multiplication", lines)
}

private fun buildDivisionSteps(aStr: String, bStr: String, result: BinaryOpResult): StepGroup {
    val a = normalizeBinary(aStr)
    val b = normalizeBinary(bStr)
    val lines = mutableListOf<StepLine>()

    lines += StepLine("A = $a")
    lines += StepLine("B = $b")

    val aVal = BigInteger(a, 2)
    val bVal = BigInteger(b, 2)

    if (bVal == BigInteger.ZERO) {
        lines += StepLine("Division by zero is not allowed.", isResult = true)
        return StepGroup("Steps - Division", lines)
    }

    if (aVal == BigInteger.ZERO) {
        lines += StepLine("0 / B = 0 remainder 0")
        lines += StepLine("Quotient = 0", isResult = true)
        lines += StepLine("Remainder = 0")
        return StepGroup("Steps - Division", lines)
    }

    val maxShift = aVal.bitLength() - bVal.bitLength()
    var remainder = aVal
    var quotient = BigInteger.ZERO

    lines += StepLine("Align B with highest bit in A and subtract when possible.")

    for (shift in maxShift downTo 0) {
        val shifted = bVal.shiftLeft(shift)
        val shiftedStr = shifted.toString(2)
        if (remainder >= shifted) {
            remainder = remainder.subtract(shifted)
            quotient = quotient.setBit(shift)
            lines += StepLine("shift $shift: remainder >= (B << $shift) = $shiftedStr, subtract -> ${remainder.toString(2)}")
        } else {
            lines += StepLine("shift $shift: remainder < (B << $shift) = $shiftedStr, keep -> ${remainder.toString(2)}")
        }
    }

    lines += StepLine("Quotient = ${normalizeBinary(result.value)}", isResult = true)
    lines += StepLine("Remainder = ${normalizeBinary(result.remainder ?: "0")}")
    return StepGroup("Steps - Division", lines)
}

private fun normalizeBinary(value: String): String {
    val trimmed = value.trimStart('0')
    return if (trimmed.isEmpty()) "0" else trimmed
}
