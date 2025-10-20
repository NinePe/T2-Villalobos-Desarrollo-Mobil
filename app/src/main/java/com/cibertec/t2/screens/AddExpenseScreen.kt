package com.cibertec.t2.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cibertec.t2.data.Category
import com.cibertec.t2.data.DatabaseHelper
import com.cibertec.t2.data.Expense
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

/**
 * Screen for registering a new expense
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onExpenseSaved: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    val scope = rememberCoroutineScope()
    
    // State variables
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(getTodayDate()) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var showAmountDialog by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    var snackbarColor by remember { mutableStateOf(Color.Green) }
    
    val categories = Category.obtenerTodas()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // DatePicker Dialog
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    
    // Show snackbar effect
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            snackbarMessage = null
        }
    }
    
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = snackbarColor,
                    contentColor = Color.White
                )
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Registrar Nuevo Gasto",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Amount Field
            Text(
                "Monto",
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            
            OutlinedTextField(
                value = amount,
                onValueChange = {
                    amount = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("\$0.00", color = Color.Gray) },
                leadingIcon = {
                    Text("\$", color = Color(0xFF98D89E), fontWeight = FontWeight.Bold)
                },
                trailingIcon = {
                    Icon(
                        Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = Color.White
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF98D89E),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFF98D89E)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            // Description Field (Optional)
            Text(
                "Descripción (opcional)",
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ej. Café con amigos", color = Color.Gray) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color(0xFF98D89E),
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color(0xFF98D89E)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            // Date Field
            Text(
                "Fecha",
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            
            OutlinedTextField(
                value = selectedDate,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                enabled = false,
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(
                            Icons.Default.CalendarToday,
                            contentDescription = "Seleccionar fecha",
                            tint = Color.White
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = Color.White,
                    disabledBorderColor = Color.Gray,
                    disabledTrailingIconColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            // Category Dropdown
            Text(
                "Categoría",
                color = Color.White,
                fontWeight = FontWeight.Medium
            )
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = {
                    expanded = !expanded
                }
            ) {
                OutlinedTextField(
                    value = selectedCategory?.nombre ?: "Seleccionar categoría",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    leadingIcon = {
                        if (selectedCategory != null) {
                            Icon(
                                selectedCategory!!.icono,
                                contentDescription = null,
                                tint = selectedCategory!!.color
                            )
                        }
                    },
                    trailingIcon = {
                        Icon(
                            if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = if (selectedCategory == null) Color.Gray else Color.White,
                        focusedBorderColor = Color(0xFF98D89E),
                        unfocusedBorderColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Color(0xFF2C2C2E))
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        category.icono,
                                        contentDescription = null,
                                        tint = category.color
                                    )
                                    Column {
                                        Text(category.nombre, color = Color.White)
                                        Text(
                                            "Límite: \$${String.format("%.2f", category.limiteMensual)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = Color.White
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Save Button
            Button(
                onClick = {
                    // Validate amount
                    val amountValue = amount.toDoubleOrNull()
                    if (amountValue == null || amountValue <= 0) {
                        showAmountDialog = true
                        return@Button
                    }
                    
                    // Validate category
                    if (selectedCategory == null) {
                        showCategoryDialog = true
                        return@Button
                    }
                    
                    // Create expense
                    val expense = Expense(
                        monto = amountValue,
                        descripcion = description.ifEmpty { selectedCategory!!.nombre },
                        fecha = selectedDate,
                        categoriaId = selectedCategory!!.id,
                        marcaDeTiempo = System.currentTimeMillis()
                    )
                    
                    // Save to database
                    val result = dbHelper.insertExpense(expense)
                    
                    if (result != -1L) {
                        // Check monthly limit
                        val monthlyTotal = dbHelper.getMonthlyTotalByCategory(selectedCategory!!.id)
                        val limit = selectedCategory!!.limiteMensual
                        
                        if (monthlyTotal > limit) {
                            snackbarMessage = "⚠️ Has excedido el límite de ${selectedCategory!!.nombre}: \$${String.format("%.2f", monthlyTotal)} de \$${String.format("%.2f", limit)}"
                            snackbarColor = Color(0xFFFF9800) // Orange
                        } else {
                            snackbarMessage = "✓ Gasto guardado correctamente"
                            snackbarColor = Color(0xFF4CAF50) // Green
                        }
                        
                        // Clear fields
                        amount = ""
                        description = ""
                        selectedDate = getTodayDate()
                        selectedCategory = null
                        
                        // Navigate to expenses list after a short delay
                        scope.launch {
                            delay(1500)
                            onExpenseSaved()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF98D89E),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Guardar Gasto",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
    
    // Amount Validation Dialog
    if (showAmountDialog) {
        AlertDialog(
            onDismissRequest = { showAmountDialog = false },
            title = {
                Text("Error de validación", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("El monto debe ser mayor a 0")
            },
            confirmButton = {
                TextButton(
                    onClick = { showAmountDialog = false }
                ) {
                    Text("Aceptar")
                }
            },
            containerColor = Color(0xFF2C2C2E),
            textContentColor = Color.White,
            titleContentColor = Color.White
        )
    }
    
    // Category Validation Dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = {
                Text("Error de validación", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Debe seleccionar una categoría")
            },
            confirmButton = {
                TextButton(
                    onClick = { showCategoryDialog = false }
                ) {
                    Text("Aceptar")
                }
            },
            containerColor = Color(0xFF2C2C2E),
            textContentColor = Color.White,
            titleContentColor = Color.White
        )
    }
}

/**
 * Helper function to get today's date in dd/MM/yyyy format
 */
private fun getTodayDate(): String {
    val calendar = Calendar.getInstance()
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
}

