package com.numberconverter.app

/**
 * All number-base conversion logic — pure Kotlin, no Android dependencies.
 * Works 100% offline.
 */

enum class Base(val label: String, val short: String, val radix: Int, val hint: String) {
    DECIMAL("Decimal",     "DEC", 10, "Digits 0–9 and ."),
    BINARY ("Binary",      "BIN", 2,  "Digits 0, 1 and ."),
    OCTAL  ("Octal",       "OCT", 8,  "Digits 0–7 and ."),
    HEX    ("Hexadecimal", "HEX", 16, "Digits 0–9, A–F and .")
}

data class ConversionResult(
    val decimal: String,
    val binary:  String,
    val octal:   String,
    val hex:     String
)

data class StepGroup(
    val title: String,
    val lines: List<StepLine>
)

data class StepLine(
    val text: String,
    val isResult: Boolean = false
)

object ConversionEngine {

    private val validators = mapOf(
        Base.DECIMAL to Regex("^[0-9]+(\\.[0-9]+)?$"),
        Base.BINARY  to Regex("^[01]+(\\.[01]+)?$"),
        Base.OCTAL   to Regex("^[0-7]+(\\.[0-7]+)?$"),
        Base.HEX     to Regex("^[0-9a-fA-F]+(\\.[0-9a-fA-F]+)?$")
    )

    fun isValid(input: String, base: Base): Boolean =
        input.isNotEmpty() && validators[base]!!.matches(input)

    fun convert(input: String, fromBase: Base): ConversionResult? {
        if (!isValid(input, fromBase)) return null
        return try {
            val parts = input.uppercase().split(".")
            val intPartStr = parts[0]
            val fracPartStr = if (parts.size > 1) parts[1] else ""

            var decInt = 0L
            if (intPartStr.isNotEmpty()) {
                decInt = intPartStr.toLong(fromBase.radix)
            }

            var decFrac = 0.0
            if (fracPartStr.isNotEmpty()) {
                if (fromBase == Base.DECIMAL) {
                    decFrac = ("0.$fracPartStr").toDouble()
                } else {
                    var factor = 1.0 / fromBase.radix
                    for (char in fracPartStr) {
                        val digit = char.digitToInt(fromBase.radix)
                        decFrac += digit * factor
                        factor /= fromBase.radix
                    }
                }
            }

            ConversionResult(
                decimal = if (fromBase == Base.DECIMAL) input else formatDecimalResult(decInt, decFrac),
                binary  = if (fromBase == Base.BINARY) input else convertToTarget(decInt, decFrac, Base.BINARY),
                octal   = if (fromBase == Base.OCTAL) input else convertToTarget(decInt, decFrac, Base.OCTAL),
                hex     = if (fromBase == Base.HEX) input.uppercase() else convertToTarget(decInt, decFrac, Base.HEX)
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun formatDecimalResult(intPart: Long, fracPart: Double): String {
        if (fracPart == 0.0) return intPart.toString()
        val formatted = String.format(java.util.Locale.US, "%.6f", fracPart)
        var fracStr = formatted.substringAfter(".")
        fracStr = fracStr.trimEnd('0')
        if (fracStr.isEmpty()) return intPart.toString()
        return "$intPart.$fracStr"
    }

    private fun convertToTarget(decInt: Long, decFrac: Double, targetBase: Base): String {
        val intStr = decInt.toString(targetBase.radix).uppercase()
        if (decFrac == 0.0) return intStr

        val maxLen = when (targetBase) {
            Base.BINARY -> 8
            Base.OCTAL -> 6
            Base.HEX -> 4
            else -> 6
        }

        var currentFrac = decFrac
        val fracDigits = StringBuilder()
        var isApprox = false

        for (i in 0 until maxLen) {
            currentFrac *= targetBase.radix
            val d = currentFrac.toLong()
            fracDigits.append(d.toString(targetBase.radix).uppercase())
            currentFrac -= d
            if (currentFrac < 1e-9) break
        }

        if (currentFrac >= 1e-9) {
            isApprox = true
        }

        val approxPrefix = if (isApprox) "≈ " else ""
        return "$approxPrefix$intStr.${fracDigits}"
    }

    private fun formatStepDouble(d: Double): String {
        if (d == 0.0) return "0.0"
        var s = String.format(java.util.Locale.US, "%.6f", d).trimEnd('0')
        if (s.endsWith(".")) s += "0"
        return s
    }

    private fun superscript(p: Int): String {
        val chars = mapOf(
            '-' to '⁻', '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³',
            '4' to '⁴', '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹'
        )
        return p.toString().map { chars[it] ?: it }.joinToString("")
    }

    fun buildSteps(input: String, fromBase: Base): List<StepGroup> {
        if (!isValid(input, fromBase)) return emptyList()
        val upper = input.uppercase()
        val parts = upper.split(".")
        val intPartStr = parts[0]
        val fracPartStr = if (parts.size > 1) parts[1] else ""

        val groups = mutableListOf<StepGroup>()
        var decInt = 0L
        var decFrac = 0.0

        if (intPartStr.isNotEmpty()) {
            decInt = intPartStr.toLong(fromBase.radix)
        }
        if (fracPartStr.isNotEmpty()) {
            if (fromBase == Base.DECIMAL) {
                decFrac = ("0.$fracPartStr").toDouble()
            } else {
                var factor = 1.0 / fromBase.radix
                for (char in fracPartStr) {
                    val digit = char.digitToInt(fromBase.radix)
                    decFrac += digit * factor
                    factor /= fromBase.radix
                }
            }
        }

        // Step 1: convert input base → decimal (if not already decimal)
        if (fromBase != Base.DECIMAL) {
            val lines = mutableListOf<StepLine>()
            
            // Int part
            if (intPartStr.isNotEmpty()) {
                val digits = intPartStr.reversed()
                when (fromBase) {
                    Base.BINARY -> digits.forEachIndexed { i, c ->
                        val v = c.digitToInt() * Math.pow(2.0, i.toDouble()).toLong()
                        lines += StepLine("$c × 2^$i  =  $v")
                    }
                    Base.OCTAL -> digits.forEachIndexed { i, c ->
                        val v = c.digitToInt() * Math.pow(8.0, i.toDouble()).toLong()
                        lines += StepLine("$c × 8^$i  =  $v")
                    }
                    Base.HEX -> digits.forEachIndexed { i, c ->
                        val v = c.digitToInt(16) * Math.pow(16.0, i.toDouble()).toLong()
                        lines += StepLine("$c (= ${c.digitToInt(16)}) × 16^$i  =  $v")
                    }
                    else -> {}
                }
                if (fracPartStr.isEmpty()) {
                    lines += StepLine("Total  =  $decInt", isResult = true)
                } else {
                    lines += StepLine("Integer total  =  $decInt")
                }
            }
            
            // Frac part
            if (fracPartStr.isNotEmpty()) {
                if (intPartStr.isNotEmpty()) lines += StepLine("") // spacing
                
                when (fromBase) {
                    Base.BINARY -> fracPartStr.forEachIndexed { i, c ->
                        val p = i + 1
                        val v = c.digitToInt() * Math.pow(2.0, -p.toDouble())
                        lines += StepLine("$c × 2${superscript(-p)}  =  ${formatStepDouble(v)}")
                    }
                    Base.OCTAL -> fracPartStr.forEachIndexed { i, c ->
                        val p = i + 1
                        val v = c.digitToInt() * Math.pow(8.0, -p.toDouble())
                        lines += StepLine("$c × 8${superscript(-p)}  =  ${formatStepDouble(v)}")
                    }
                    Base.HEX -> fracPartStr.forEachIndexed { i, c ->
                        val p = i + 1
                        val v = c.digitToInt(16) * Math.pow(16.0, -p.toDouble())
                        lines += StepLine("$c (= ${c.digitToInt(16)}) × 16${superscript(-p)}  =  ${formatStepDouble(v)}")
                    }
                    else -> {}
                }
                lines += StepLine("Fractional total  =  ${formatStepDouble(decFrac)}")
                if (intPartStr.isNotEmpty()) {
                    lines += StepLine("Combined Result  =  ${formatDecimalResult(decInt, decFrac)}", isResult = true)
                } else {
                    lines += StepLine("Result  =  ${formatDecimalResult(decInt, decFrac)}", isResult = true)
                }
            }
            
            groups += StepGroup("Step 1 — ${fromBase.label} to Decimal", lines)
        }

        // Steps: decimal → each other base
        val targets = Base.values().filter { it != fromBase && it != Base.DECIMAL }
        val all = if (fromBase == Base.DECIMAL)
            Base.values().filter { it != Base.DECIMAL }
        else targets

        all.forEach { target ->
            groups += buildTargetSteps(decInt, decFrac, target)
        }
        return groups
    }

    private fun buildTargetSteps(decInt: Long, decFrac: Double, target: Base): StepGroup {
        val lines = mutableListOf<StepLine>()
        val remainders = mutableListOf<String>()
        var n = decInt
        
        if (n == 0L) {
            lines += StepLine("Integer result  =  0")
        } else {
            while (n > 0) {
                val rem = n % target.radix
                val remStr = rem.toString(target.radix).uppercase()
                lines += StepLine("$n ÷ ${target.radix}  =  ${n / target.radix}   remainder $remStr")
                remainders += remStr
                n /= target.radix
            }
            lines += StepLine("Integer result (read bottom-up)  →  ${remainders.reversed().joinToString("")}")
        }
        
        val intResultStr = if (decInt == 0L) "0" else remainders.reversed().joinToString("")
        
        if (decFrac > 0.0) {
            lines += StepLine("")
            
            val maxLen = when (target) {
                Base.BINARY -> 8
                Base.OCTAL -> 6
                Base.HEX -> 4
                else -> 6
            }
            
            var currentFrac = decFrac
            val fracDigits = StringBuilder()
            for (i in 0 until maxLen) {
                val mult = currentFrac * target.radix
                val d = mult.toLong()
                val dStr = d.toString(target.radix).uppercase()
                
                lines += StepLine("${formatStepDouble(currentFrac)} × ${target.radix}  =  ${formatStepDouble(mult)}  → integer digit: $dStr")
                
                fracDigits.append(dStr)
                currentFrac = mult - d
                if (currentFrac < 1e-9) break
            }
            
            lines += StepLine("Fractional result (read top-down)  →  .$fracDigits")
            lines += StepLine("Combined Result  =  $intResultStr.$fracDigits", isResult = true)
        } else {
            lines += StepLine("Result  =  $intResultStr", isResult = true)
        }
        
        return StepGroup("Decimal ${formatDecimalResult(decInt, decFrac)} → ${target.label}", lines)
    }
}
