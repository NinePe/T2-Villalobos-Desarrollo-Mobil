package com.cibertec.t2.data

/**
 * Data class representing an expense entry
 */
data class Expense(
    val id: Long = 0L,
    val monto: Double,
    val descripcion: String,
    val fecha: String,              // Formato: dd/MM/yyyy
    val categoriaId: Int,
    val marcaDeTiempo: Long = System.currentTimeMillis() // Para ordenar por más reciente
)

