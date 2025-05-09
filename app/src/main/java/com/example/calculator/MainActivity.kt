package com.example.calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.calculator.ui.theme.CalculatorTheme
import androidx.compose.foundation.background



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalculatorScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(modifier: Modifier = Modifier) {
    var inputText by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }

    val buttons = listOf(
        listOf("7", "8", "9", "/"),
        listOf("4", "5", "6", "*"),
        listOf("1", "2", "3", "-"),
        listOf("0", "C", "=", "+")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color(0xFFF5F5F5)),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = inputText,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Gray,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.Black,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            buttons.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    row.forEach { label ->
                        val buttonColor = when (label) {
                            "/", "*", "-", "+" -> Color(0xFF4CAF50)
                            "=" -> Color(0xFFF44336)
                            "C" -> Color(0xFF2196F3)
                            else -> Color.DarkGray
                        }

                        Button(
                            onClick = {
                                when (label) {
                                    "=" -> {
                                        try {
                                            resultText = evaluateExpression(inputText)
                                        } catch (e: Exception) {
                                            resultText = "Error"
                                        }
                                    }
                                    "C" -> {
                                        inputText = ""
                                        resultText = ""
                                    }
                                    else -> {
                                        inputText += label
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonColor,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        ) {
                            Text(text = label, style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}


fun evaluateExpression(expression: String): String {
    return try {
        val tokens = tokenize(expression)
        val result = evaluateTokens(tokens)
        result.toString()
    } catch (e: Exception) {
        "Error"
    }
}

fun tokenize(expression: String): List<String> {
    val tokens = mutableListOf<String>()
    var current = ""
    for (c in expression) {
        if (c.isDigit() || c == '.') {
            current += c
        } else if (c in listOf('+', '-', '*', '/')) {
            if (current.isNotEmpty()) {
                tokens.add(current)
                current = ""
            }
            tokens.add(c.toString())
        }
    }
    if (current.isNotEmpty()) {
        tokens.add(current)
    }
    return tokens
}

fun evaluateTokens(tokens: List<String>): Double {
    val stack = mutableListOf<Double>()
    var currentOp: String? = null

    for (token in tokens) {
        when (token) {
            "+", "-", "*", "/" -> currentOp = token
            else -> {
                val number = token.toDouble()
                if (currentOp == null) {
                    stack.add(number)
                } else {
                    val last = stack.removeAt(stack.lastIndex)
                    val result = when (currentOp) {
                        "+" -> last + number
                        "-" -> last - number
                        "*" -> last * number
                        "/" -> last / number
                        else -> number
                    }
                    stack.add(result)
                    currentOp = null
                }
            }
        }
    }
    return stack.first()
}

@Preview(showBackground = true)
@Composable
fun CalculatorScreenPreview() {
    CalculatorTheme {
        CalculatorScreen()
    }
}