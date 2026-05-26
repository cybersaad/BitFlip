package com.bitflip.app

import kotlin.math.*

/**
 * Expression evaluator for the scientific calculator.
 * Supports: +, -, *, /, ^ (power), ! (factorial), % (percentage)
 * Functions: sin, cos, tan, asin, acos, atan, ln, log, sqrt, abs
 * Constants: π, e
 * Unicode: ×, ÷, √, ²
 */
object CalculatorEngine {

    private sealed class Token {
        data class Num(val value: Double) : Token()
        data class Op(val op: Char) : Token()
        data class Func(val name: String) : Token()
        object LParen : Token()
        object RParen : Token()
        object Factorial : Token()
        object Percent : Token()
    }

    fun evaluate(expression: String, useDegrees: Boolean = true): Result<Double> {
        return try {
            val cleaned = expression.trim()
            if (cleaned.isEmpty()) return Result.failure(Exception("Empty"))

            val tokens = tokenize(cleaned)
            if (tokens.isEmpty()) return Result.failure(Exception("Empty"))

            val parser = ExprParser(tokens, useDegrees)
            val result = parser.parseExpression()

            if (!parser.isAtEnd()) throw ArithmeticException("Unexpected characters")
            if (result.isNaN() || result.isInfinite()) throw ArithmeticException("Math error")

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun formatResult(value: Double): String {
        if (value.isNaN()) return "Error"
        if (value.isInfinite()) return if (value > 0) "∞" else "-∞"
        if (value == 0.0) return "0"

        // Check if value is very close to an integer
        val nearInt = Math.round(value)
        if (abs(value - nearInt.toDouble()) < 1e-9 && abs(nearInt.toDouble()) < 1e15) {
            return nearInt.toString()
        }

        return if (abs(value) >= 1e15 || abs(value) < 1e-10) {
            val sci = String.format("%.6E", value)
            val parts = sci.split("E")
            val mantissa = parts[0].trimEnd('0').trimEnd('.')
            "${mantissa}E${parts[1]}"
        } else {
            String.format("%.10f", value).trimEnd('0').trimEnd('.')
        }
    }

    // ── Tokenizer ──────────────────────────────────────────────

    private fun tokenize(expr: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0

        while (i < expr.length) {
            val c = expr[i]
            when {
                c.isWhitespace() -> i++

                c.isDigit() || (c == '.' && i + 1 < expr.length && expr[i + 1].isDigit()) -> {
                    maybeImplicitMultiply(tokens)
                    val start = i
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) i++
                    tokens.add(Token.Num(expr.substring(start, i).toDouble()))
                }

                c == '+' -> { tokens.add(Token.Op('+')); i++ }
                c == '-' -> { tokens.add(Token.Op('-')); i++ }
                c == '*' || c == '×' -> { tokens.add(Token.Op('*')); i++ }
                c == '/' || c == '÷' -> { tokens.add(Token.Op('/')); i++ }
                c == '^' -> { tokens.add(Token.Op('^')); i++ }

                c == '²' -> {
                    tokens.add(Token.Op('^'))
                    tokens.add(Token.Num(2.0))
                    i++
                }

                c == '(' -> {
                    maybeImplicitMultiply(tokens)
                    tokens.add(Token.LParen)
                    i++
                }
                c == ')' -> { tokens.add(Token.RParen); i++ }

                c == '!' -> { tokens.add(Token.Factorial); i++ }
                c == '%' -> { tokens.add(Token.Percent); i++ }

                c == 'π' -> {
                    maybeImplicitMultiply(tokens)
                    tokens.add(Token.Num(Math.PI))
                    i++
                }

                c == '√' -> {
                    maybeImplicitMultiply(tokens)
                    tokens.add(Token.Func("sqrt"))
                    i++
                }

                c.isLetter() -> {
                    maybeImplicitMultiply(tokens)
                    val start = i
                    while (i < expr.length && expr[i].isLetter()) i++
                    val word = expr.substring(start, i)
                    when (word) {
                        "sin", "cos", "tan", "asin", "acos", "atan",
                        "ln", "log", "sqrt", "abs", "exp" -> tokens.add(Token.Func(word))
                        "pi" -> tokens.add(Token.Num(Math.PI))
                        "e" -> tokens.add(Token.Num(Math.E))
                        else -> throw ArithmeticException("Unknown: $word")
                    }
                }

                else -> throw ArithmeticException("Unknown character: '$c'")
            }
        }
        return tokens
    }

    /**
     * Insert implicit multiplication when appropriate.
     * E.g., "2π" → "2 * π", "(2)(3)" → "(2) * (3)"
     */
    private fun maybeImplicitMultiply(tokens: MutableList<Token>) {
        if (tokens.isEmpty()) return
        val last = tokens.last()
        if (last is Token.Num || last is Token.RParen ||
            last is Token.Factorial || last is Token.Percent
        ) {
            tokens.add(Token.Op('*'))
        }
    }

    // ── Recursive Descent Parser ───────────────────────────────

    private class ExprParser(
        private val tokens: List<Token>,
        private val useDegrees: Boolean
    ) {
        private var pos = 0

        fun isAtEnd() = pos >= tokens.size
        private fun peek(): Token? = tokens.getOrNull(pos)
        private fun advance(): Token = tokens[pos++]

        // expression = term (('+' | '-') term)*
        fun parseExpression(): Double {
            var left = parseTerm()
            while (true) {
                val op = peek() as? Token.Op ?: break
                if (op.op != '+' && op.op != '-') break
                advance()
                val right = parseTerm()
                left = if (op.op == '+') left + right else left - right
            }
            return left
        }

        // term = power (('*' | '/') power)*
        private fun parseTerm(): Double {
            var left = parsePower()
            while (true) {
                val op = peek() as? Token.Op ?: break
                if (op.op != '*' && op.op != '/') break
                advance()
                val right = parsePower()
                left = if (op.op == '*') left * right else {
                    if (right == 0.0) throw ArithmeticException("Division by zero")
                    left / right
                }
            }
            return left
        }

        // power = unary ('^' power)?  (right-associative)
        private fun parsePower(): Double {
            val base = parseUnary()
            val op = peek() as? Token.Op
            if (op != null && op.op == '^') {
                advance()
                val exp = parsePower()
                return base.pow(exp)
            }
            return base
        }

        // unary = ('-' | '+') unary | postfix
        private fun parseUnary(): Double {
            val op = peek() as? Token.Op
            if (op != null && (op.op == '-' || op.op == '+')) {
                advance()
                val value = parseUnary()
                return if (op.op == '-') -value else value
            }
            return parsePostfix()
        }

        // postfix = primary ('!' | '%')*
        private fun parsePostfix(): Double {
            var value = parsePrimary()
            while (true) {
                when (peek()) {
                    is Token.Factorial -> { advance(); value = factorial(value) }
                    is Token.Percent -> { advance(); value /= 100.0 }
                    else -> break
                }
            }
            return value
        }

        // primary = number | '(' expression ')' | function '(' expression ')'
        private fun parsePrimary(): Double {
            return when (val token = peek()) {
                is Token.Num -> { advance(); token.value }
                is Token.LParen -> {
                    advance()
                    val value = parseExpression()
                    if (peek() is Token.RParen) advance()
                    // Allow missing closing paren for real-time preview
                    value
                }
                is Token.Func -> {
                    advance()
                    if (peek() !is Token.LParen) throw ArithmeticException("Expected (")
                    advance()
                    val arg = parseExpression()
                    if (peek() is Token.RParen) advance()
                    applyFunc(token.name, arg)
                }
                null -> throw ArithmeticException("Unexpected end")
                else -> throw ArithmeticException("Unexpected token")
            }
        }

        private fun toRad(v: Double) = if (useDegrees) Math.toRadians(v) else v
        private fun fromRad(v: Double) = if (useDegrees) Math.toDegrees(v) else v

        private fun applyFunc(name: String, arg: Double): Double = when (name) {
            "sin" -> { val r = sin(toRad(arg)); if (abs(r) < 1e-14) 0.0 else r }
            "cos" -> { val r = cos(toRad(arg)); if (abs(r) < 1e-14) 0.0 else r }
            "tan" -> {
                val r = toRad(arg)
                if (abs(cos(r)) < 1e-14) throw ArithmeticException("tan undefined")
                val result = tan(r); if (abs(result) < 1e-14) 0.0 else result
            }
            "asin" -> {
                if (arg < -1.0 || arg > 1.0) throw ArithmeticException("asin domain error")
                fromRad(asin(arg))
            }
            "acos" -> {
                if (arg < -1.0 || arg > 1.0) throw ArithmeticException("acos domain error")
                fromRad(acos(arg))
            }
            "atan" -> fromRad(atan(arg))
            "ln" -> {
                if (arg <= 0) throw ArithmeticException("ln domain error")
                ln(arg)
            }
            "log" -> {
                if (arg <= 0) throw ArithmeticException("log domain error")
                log10(arg)
            }
            "sqrt" -> {
                if (arg < 0) throw ArithmeticException("√ domain error")
                sqrt(arg)
            }
            "abs" -> abs(arg)
            "exp" -> exp(arg)
            else -> throw ArithmeticException("Unknown function: $name")
        }

        private fun factorial(n: Double): Double {
            if (n < 0 || n != floor(n)) throw ArithmeticException("Factorial needs non-negative integer")
            if (n > 170) throw ArithmeticException("Too large")
            var r = 1.0
            for (i in 2..n.toInt()) r *= i
            return r
        }
    }
}
