package com.cibertec.t2.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*

/**
 * SQLite Database Helper for managing expense data
 */
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MisFinanzas.db"
        private const val DATABASE_VERSION = 1
        
        // Table name
        private const val TABLE_EXPENSES = "expenses"
        
        // Column names
        private const val COLUMN_ID = "id"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_CATEGORY_ID = "category_id"
        private const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_EXPENSES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_AMOUNT REAL NOT NULL,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_DATE TEXT NOT NULL,
                $COLUMN_CATEGORY_ID INTEGER NOT NULL,
                $COLUMN_TIMESTAMP INTEGER NOT NULL
            )
        """.trimIndent()
        
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        onCreate(db)
    }

    /**
     * Insert a new expense into the database
     * @return the row ID of the newly inserted row, or -1 if an error occurred
     */
    fun insertExpense(expense: Expense): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_AMOUNT, expense.monto)
            put(COLUMN_DESCRIPTION, expense.descripcion)
            put(COLUMN_DATE, expense.fecha)
            put(COLUMN_CATEGORY_ID, expense.categoriaId)
            put(COLUMN_TIMESTAMP, expense.marcaDeTiempo)
        }
        
        val id = db.insert(TABLE_EXPENSES, null, values)
        db.close()
        return id
    }

    /**
     * Get all expenses, sorted by most recent first
     */
    fun getAllExpenses(): List<Expense> {
        val expenses = mutableListOf<Expense>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_EXPENSES,
            null,
            null,
            null,
            null,
            null,
            "$COLUMN_TIMESTAMP DESC" // Sort by most recent
        )

        if (cursor.moveToFirst()) {
            do {
                val expense = Expense(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    monto = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                    descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)) ?: "",
                    fecha = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                    categoriaId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)),
                    marcaDeTiempo = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                )
                expenses.add(expense)
            } while (cursor.moveToNext())
        }
        
        cursor.close()
        db.close()
        return expenses
    }

    /**
     * Delete an expense by ID
     * @return the number of rows affected
     */
    fun deleteExpense(id: Long): Int {
        val db = writableDatabase
        val rowsDeleted = db.delete(TABLE_EXPENSES, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsDeleted
    }

    /**
     * Calculate total expenses for a specific category in the current month
     */
    fun getMonthlyTotalByCategory(categoryId: Int): Double {
        var total = 0.0
        val db = readableDatabase
        
        // Get current month and year
        val calendar = Calendar.getInstance()
        val currentMonth = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH is 0-based
        val currentYear = calendar.get(Calendar.YEAR)
        
        val cursor = db.query(
            TABLE_EXPENSES,
            arrayOf(COLUMN_AMOUNT, COLUMN_DATE),
            "$COLUMN_CATEGORY_ID = ?",
            arrayOf(categoryId.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val dateString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
                
                // Parse date to check if it's in current month
                try {
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val date = sdf.parse(dateString)
                    date?.let {
                        val expenseCalendar = Calendar.getInstance()
                        expenseCalendar.time = it
                        val expenseMonth = expenseCalendar.get(Calendar.MONTH) + 1
                        val expenseYear = expenseCalendar.get(Calendar.YEAR)
                        
                        if (expenseMonth == currentMonth && expenseYear == currentYear) {
                            total += amount
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } while (cursor.moveToNext())
        }
        
        cursor.close()
        db.close()
        return total
    }

    /**
     * Calculate total of all expenses
     */
    fun getTotalExpenses(): Double {
        var total = 0.0
        val db = readableDatabase
        val cursor = db.query(
            TABLE_EXPENSES,
            arrayOf(COLUMN_AMOUNT),
            null,
            null,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                total += cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
            } while (cursor.moveToNext())
        }
        
        cursor.close()
        db.close()
        return total
    }
}

