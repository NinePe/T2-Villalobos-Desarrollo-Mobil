package com.cibertec.t2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.cibertec.t2.screens.AddExpenseScreen
import com.cibertec.t2.screens.ExpensesListScreen
import com.cibertec.t2.ui.theme.T2Theme

/**
 * Main Activity for MisFinanzas App
 * Handles navigation between AddExpense and ExpensesList screens
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            T2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MisFinanzasApp()
                }
            }
        }
    }
}

/**
 * Main app composable with navigation
 */
@Composable
fun MisFinanzasApp() {
    // Navigation state
    var currentScreen by remember { mutableStateOf(Screen.ExpensesList) }
    
    when (currentScreen) {
        Screen.ExpensesList -> {
            ExpensesListScreen(
                onAddExpenseClick = {
                    currentScreen = Screen.AddExpense
                }
            )
        }
        Screen.AddExpense -> {
            AddExpenseScreen(
                onExpenseSaved = {
                    currentScreen = Screen.ExpensesList
                },
                onNavigateBack = {
                    currentScreen = Screen.ExpensesList
                }
            )
        }
    }
}

/**
 * Screen destinations
 */
enum class Screen {
    ExpensesList,
    AddExpense
}
