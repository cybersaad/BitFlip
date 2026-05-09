package com.numberconverter.app

/**
 * All number-base conversion logic — pure Kotlin, no Android dependencies.
 * Works 100% offline.
 */

enum class Base(val label: String, val short: String, val radix: Int, val hint: String) {
    DECIMAL("Decimal",     "DEC", 10, "Digits 0–9"),
    BINARY ("Binary",      "BIN", 2,  "Digits 0 and 1"),
    OCTAL  ("Octal",       "OCT", 8,  "Digits 0–7"),
    HEX    ("Hexadecimal", "HEX", 16, "Digits 0–9 and A–F")
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
        Base.DECIMAL to Regex("^[0-9]+$"),
        Base.BINARY  to Regex("^[01]+$"),
        Base.OCTAL   to Regex("^[0-7]+$"),
        Base.HEX     to Regex("^[0-9a-fA-F]+$")
    )

    fun isValid(input: String, base: Base): Boolean =
        input.isNotEmpty() && validators[base]!!.matches(input)

    fun convert(input: String, fromBase: Base): ConversionResult? {
        if (!isValid(input, fromBase)) return null
        return try {
            val dec = input.uppercase().toLong(fromBase.radix)
            ConversionResult(
                decimal = dec.toString(10),
                binary  = dec.toString(2),
                octal   = dec.toString(8),
                hex     = dec.toString(16).uppercase()
            )
        } catch (e: NumberFormatException) {
            null
        }
    }

    fun buildSteps(input: String, fromBase: Base): List<StepGroup> {
        if (!isValid(input, fromBase)) return emptyList()
        val upper = input.uppercase()
        val dec = upper.toLong(fromBase.radix)
        val groups = mutableListOf<StepGroup>()

        // Step 1: convert input base → decimal (if not already decimal)
        if (fromBase != Base.DECIMAL) {
            val lines = mutableListOf<StepLine>()
            val digits = upper.reversed()
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
            lines += StepLine("Total  =  $dec", isResult = true)
            groups += StepGroup("Step 1 — ${fromBase.label} to Decimal", lines)
        }

        // Steps: decimal → each other base via repeated division
        val targets = Base.values().filter { it != fromBase && it != Base.DECIMAL }
        val all = if (fromBase == Base.DECIMAL)
            Base.values().filter { it != Base.DECIMAL }
        else targets

        all.forEach { target ->
            groups += divisionSteps(dec, target)
        }
        return groups
    }

    private fun divisionSteps(dec: Long, target: Base): StepGroup {
        val lines = mutableListOf<StepLine>()
        if (dec == 0L) {
            lines += StepLine("Result  =  0", isResult = true)
            return StepGroup("Decimal $dec → ${target.label}", lines)
        }
        var n = dec
        val remainders = mutableListOf<String>()
        while (n > 0) {
            val rem = n % target.radix
            val remStr = rem.toString(target.radix).uppercase()
            lines += StepLine("$n ÷ ${target.radix}  =  ${n / target.radix}   remainder $remStr")
            remainders += remStr
            n /= target.radix
        }
        lines += StepLine("Read bottom-up  →  ${remainders.reversed().joinToString("")}", isResult = true)
        return StepGroup("Decimal $dec → ${target.label}", lines)
    }
}
