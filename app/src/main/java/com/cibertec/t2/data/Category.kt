package com.cibertec.t2.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/** Categoría de gasto con icono, límite y color. */
@Immutable
data class Category(
    val id: Int,
    val nombre: String,
    val limiteMensual: Double,
    val icono: ImageVector,
    val color: Color
) {
    companion object {
        // Instancias predefinidas
        val ALIMENTACION = Category(1, "Alimentación", 800.0, Icons.Default.ShoppingCart, Color(0xFF4CAF50))
        val TRANSPORTE = Category(2, "Transporte", 300.0, Icons.Default.DirectionsCar, Color(0xFF2196F3))
        val ENTRETENIMIENTO = Category(3, "Entretenimiento", 200.0, Icons.Default.Movie, Color(0xFF9C27B0))
        val VIVIENDA = Category(4, "Vivienda", 1500.0, Icons.Default.Home, Color(0xFFFF9800))
        val SALUD = Category(5, "Salud", 400.0, Icons.Default.MedicalServices, Color(0xFFF44336))
        val CAFE_BEBIDAS = Category(6, "Café/Bebidas", 150.0, Icons.Default.Restaurant, Color(0xFF795548))
        val COMPRAS = Category(7, "Compras", 500.0, Icons.Default.LocalMall, Color(0xFFE91E63))
        val OTROS = Category(8, "Otros", 300.0, Icons.Default.Category, Color(0xFF607D8B))

        // Colecciones inmutables y mapas de búsqueda
        private val LISTA: List<Category> = listOf(
            ALIMENTACION, TRANSPORTE, ENTRETENIMIENTO, VIVIENDA,
            SALUD, CAFE_BEBIDAS, COMPRAS, OTROS
        )
        private val POR_ID: Map<Int, Category> = LISTA.associateBy { it.id }
        private val POR_NOMBRE: Map<String, Category> = LISTA.associateBy { it.nombre }

        fun obtenerTodas(): List<Category> = LISTA
        fun obtenerPorId(id: Int): Category? = POR_ID[id]
        fun obtenerPorNombre(nombre: String): Category? = POR_NOMBRE[nombre]
    }
}

