package com.cibertec.t2.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cibertec.t2.data.Category
import com.cibertec.t2.data.DatabaseHelper
import com.cibertec.t2.data.Expense
import kotlinx.coroutines.launch

/**
 * Screen for displaying list of expenses
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesListScreen(
    onAddExpenseClick: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    
    // State variables
    var expenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedExpense by remember { mutableStateOf<Expense?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Load expenses
    LaunchedEffect(Unit) {
        expenses = dbHelper.getAllExpenses()
    }
    
    // Calculate total
    val totalExpenses = expenses.sumOf { it.monto }
    
    // Bottom Sheet state
    val sheetState = rememberModalBottomSheetState()
    
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mis Gastos",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                actions = {
                    FloatingActionButton(
                        onClick = onAddExpenseClick,
                        containerColor = Color(0xFF98D89E),
                        contentColor = Color.Black,
                        shape = CircleShape,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(56.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Agregar gasto",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1C1C1E)
                )
            )
        },
        containerColor = Color(0xFF1C1C1E)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter buttons (placeholder for future implementation)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = true,
                    onClick = { },
                    label = { Text("Esta semana") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF98D89E).copy(alpha = 0.3f),
                        selectedLabelColor = Color(0xFF98D89E),
                        selectedLeadingIconColor = Color(0xFF98D89E),
                        containerColor = Color(0xFF2C2C2E),
                        labelColor = Color.White,
                        iconColor = Color.White
                    )
                )
                
                FilterChip(
                    selected = false,
                    onClick = { },
                    label = { Text("Categoría") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF98D89E).copy(alpha = 0.3f),
                        selectedLabelColor = Color(0xFF98D89E),
                        containerColor = Color(0xFF2C2C2E),
                        labelColor = Color.White,
                        iconColor = Color.White
                    )
                )
            }
            
            // Expenses List
            if (expenses.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "No hay gastos registrado en el sistema",
                            color = Color.Gray,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "Presiona agregue uno",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(expenses, key = { it.id }) { expense ->
                        ExpenseItem(
                            expense = expense,
                            onClick = {
                                selectedExpense = expense
                                showBottomSheet = true
                            }
                        )
                    }
                }
            }
            
            // Total Display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF2C2C2E)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Total:",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "-\$${String.format("%.2f", totalExpenses)}",
                        color = Color(0xFFFF5252),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
    
    // Bottom Sheet for Delete/Cancel
    if (showBottomSheet && selectedExpense != null) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFF2C2C2E)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Delete option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showBottomSheet = false
                            showDeleteDialog = true
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "Eliminar",
                        color = Color.Red,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                Divider(color = Color.Gray.copy(alpha = 0.3f))
                
                // Cancel option
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showBottomSheet = false
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        "Cancelar",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
    
    // Delete Confirmation Dialog
    if (showDeleteDialog && selectedExpense != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text("Eliminar gasto", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("¿Eliminar este gasto de \$${String.format("%.2f", selectedExpense!!.monto)}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Delete expense
                        dbHelper.deleteExpense(selectedExpense!!.id)
                        
                        // Refresh list
                        expenses = dbHelper.getAllExpenses()
                        
                        // Show snackbar
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Gasto eliminado",
                                duration = SnackbarDuration.Short
                            )
                        }
                        
                        showDeleteDialog = false
                        selectedExpense = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.Red
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            },
            containerColor = Color(0xFF2C2C2E),
            textContentColor = Color.White,
            titleContentColor = Color.White
        )
    }
}

/**
 * Composable for individual expense item
 */
@Composable
fun ExpenseItem(
    expense: Expense,
    onClick: () -> Unit
) {
    val category = Category.obtenerPorId(expense.categoriaId)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2E)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(category?.color?.copy(alpha = 0.2f) ?: Color.Gray.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    category?.icono ?: Icons.Default.Category,
                    contentDescription = null,
                    tint = category?.color ?: Color.Gray,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            // Expense Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    expense.descripcion,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    expense.fecha,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Amount
            Text(
                "-\$${String.format("%.2f", expense.monto)}",
                color = Color(0xFFFF5252),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

