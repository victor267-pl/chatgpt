package com.victor.liquidglasscalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(8, 12, 25)
        window.navigationBarColor = android.graphics.Color.rgb(8, 12, 25)
        setContent { CalculatorScreen() }
    }
}

private val bgTop = Color(0xFF17243D)
private val bgBottom = Color(0xFF080C19)
private val glass = Color(0x26FFFFFF)
private val cyan = Color(0xFF75DDF5)
private val violet = Color(0xFFB7A1FF)

@Composable
private fun CalculatorScreen() {
    var expression by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("0") }
    var degrees by remember { mutableStateOf(true) }
    var justEvaluated by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf(false) }

    fun press(key: String) {
        when (key) {
            "AC" -> { expression = ""; result = "0"; error = false; justEvaluated = false }
            "⌫" -> { expression = expression.dropLast(1); error = false; justEvaluated = false }
            "=" -> {
                if (expression.isNotBlank()) {
                    try {
                        result = formatResult(ExpressionParser(expression, degrees).parse())
                        error = false
                        justEvaluated = true
                    } catch (_: Exception) {
                        result = "Erro"
                        error = true
                        justEvaluated = true
                    }
                }
            }
            "DEG" -> degrees = !degrees
            else -> {
                if (justEvaluated && key.firstOrNull()?.isDigit() == true) expression = ""
                if (justEvaluated && key in listOf("+", "−", "×", "÷", "^")) { /* continue from expression */ }
                expression += when (key) {
                    "×" -> "*"; "÷" -> "/"; "−" -> "-"; "π" -> "pi"
                    "√" -> "sqrt("; "x²" -> "^2"; "xʸ" -> "^"
                    "sin", "cos", "tan", "ln", "log", "abs" -> "$key("
                    else -> key
                }
                error = false
                justEvaluated = false
            }
        }
    }

    val rows = listOf(
        listOf("sin", "cos", "tan", "ln", "log"),
        listOf("√", "x²", "xʸ", "(", ")"),
        listOf("AC", "⌫", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "−"),
        listOf("1", "2", "3", "+"),
        listOf("DEG", "0", ".", "=")
    )

    Box(
        modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(bgTop, Color(0xFF101A30), bgBottom)))
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("LIQUID GLASS", color = Color.White.copy(alpha = .58f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text("Calculator", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.SemiBold)
                }
                Box(Modifier.clip(RoundedCornerShape(50)).background(glass).border(1.dp, Color.White.copy(.15f), RoundedCornerShape(50)).padding(horizontal = 13.dp, vertical = 8.dp)) {
                    Text("SCIENTIFIC", color = cyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
            Spacer(Modifier.weight(1f))
            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp))
                    .background(Brush.linearGradient(listOf(Color(0x33FFFFFF), Color(0x0DFFFFFF))))
                    .border(1.dp, Color.White.copy(.17f), RoundedCornerShape(28.dp))
                    .padding(horizontal = 20.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(if (expression.isEmpty()) " " else expression.replace("*", " × ").replace("/", " ÷ ").replace("-", " − "), color = Color.White.copy(.58f), fontSize = 19.sp, textAlign = TextAlign.End, maxLines = 2)
                Spacer(Modifier.height(12.dp))
                Text(if (error) "Expressão inválida" else result, color = if (error) Color(0xFFFF8D9B) else Color.White, fontSize = 42.sp, fontWeight = FontWeight.Light, textAlign = TextAlign.End, maxLines = 1)
            }
            rows.forEachIndexed { rowIndex, row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { key ->
                        val isOperator = key in listOf("÷", "×", "−", "+", "=")
                        val isUtility = key in listOf("AC", "⌫", "DEG", "%")
                        val isScientific = rowIndex < 2
                        val color = when {
                            key == "=" -> cyan
                            isOperator -> violet
                            isUtility -> Color(0xFF9EB7D8)
                            isScientific -> Color(0xFFBBD7F1)
                            else -> Color.White
                        }
                        val size = if (isScientific) 15.sp else 21.sp
                        Box(
                            Modifier.weight(1f).height(if (isScientific) 49.dp else 58.dp)
                                .clip(RoundedCornerShape(19.dp))
                                .background(when {
                                    key == "=" -> Brush.linearGradient(listOf(Color(0xFF9CEBFA), Color(0xFF62B9E8)))
                                    isOperator -> Brush.linearGradient(listOf(Color(0x44B7A1FF), Color(0x229B86E8)))
                                    else -> Brush.linearGradient(listOf(Color(0x2EFFFFFF), Color(0x12FFFFFF)))
                                })
                                .border(1.dp, Color.White.copy(if (key == "=") .48f else .13f), RoundedCornerShape(19.dp))
                                .clickable { press(key) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (key == "DEG" && !degrees) "RAD" else key, color = if (key == "=") Color(0xFF10243A) else color, fontSize = size, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("π  •  e  •  parênteses  •  porcentagem", color = Color.White.copy(.38f), fontSize = 10.sp)
                Text("v1.0", color = Color.White.copy(.3f), fontSize = 10.sp)
            }
        }
    }
}

private fun formatResult(value: Double): String {
    require(value.isFinite())
    if (abs(value) < 1e-12) return "0"
    val rounded = if (abs(value) < 1e12) value.toBigDecimal().setScale(10, java.math.RoundingMode.HALF_UP).stripTrailingZeros().toPlainString() else value.toString()
    return rounded
}

private class ExpressionParser(private val input: String, private val degrees: Boolean) {
    private var pos = 0
    private fun skip() { while (pos < input.length && input[pos].isWhitespace()) pos++ }
    private fun eat(c: Char): Boolean { skip(); if (pos < input.length && input[pos] == c) { pos++; return true }; return false }
    private fun identifier(): String {
        skip(); val start = pos
        while (pos < input.length && input[pos].isLetter()) pos++
        return input.substring(start, pos).lowercase()
    }
    fun parse(): Double {
        val value = expression()
        skip()
        if (pos != input.length) error("Unexpected token")
        return value
    }
    private fun expression(): Double {
        var x = term()
        while (true) x = when { eat('+') -> x + term(); eat('-') -> x - term(); else -> return x }
    }
    private fun term(): Double {
        var x = power()
        while (true) x = when {
            eat('*') -> x * power()
            eat('/') -> x / power()
            eat('%') -> x / 100.0
            else -> return x
        }
    }
    private fun power(): Double {
        var x = unary()
        if (eat('^')) x = x.pow(power())
        return x
    }
    private fun unary(): Double {
        if (eat('+')) return unary()
        if (eat('-')) return -unary()
        return postfix()
    }
    private fun postfix(): Double {
        var x = primary()
        while (eat('!')) { require(x >= 0 && x <= 170 && x % 1.0 == 0.0); var n = 1.0; for (i in 2..x.toInt()) n *= i; x = n }
        return x
    }
    private fun primary(): Double {
        skip()
        if (eat('(')) { val x = expression(); require(eat(')')); return x }
        if (pos < input.length && (input[pos].isDigit() || input[pos] == '.')) {
            val start = pos
            while (pos < input.length && (input[pos].isDigit() || input[pos] == '.')) pos++
            return input.substring(start, pos).toDouble()
        }
        val name = identifier()
        if (name.isEmpty()) error("Value expected")
        if (name == "pi") return PI
        if (name == "e") return E
        require(eat('('))
        val a = expression()
        val b = if (eat(',')) expression() else 0.0
        require(eat(')'))
        val angle = if (degrees) Math.toRadians(a) else a
        return when (name) {
            "sqrt" -> sqrt(a); "sin" -> sin(angle); "cos" -> cos(angle); "tan" -> tan(angle)
            "ln" -> ln(a); "log" -> log10(a); "abs" -> abs(a)
            "min" -> min(a, b); "max" -> max(a, b)
            else -> error("Unknown function")
        }
    }
}
