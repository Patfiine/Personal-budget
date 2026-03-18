package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "finance.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, amount REAL, category TEXT, date TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS transactions")
        onCreate(db)
    }

    fun addTransaction(type: String, amount: Double, category: String, date: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("type", type)
            put("amount", amount)
            put("category", category)
            put("date", date)
        }
        db.insert("transactions", null, values)
        db.close()
    }

    fun getAllTransactions(): MutableList<Transaction> {
        val list = mutableListOf<Transaction>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM transactions", null)
        while (cursor.moveToNext()) {
            list.add(
                Transaction(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getString(3),
                    cursor.getString(4)
                )
            )
        }
        cursor.close()
        return list
    }

    fun getTransactionsByType(type: String): List<Transaction> {
        val list = mutableListOf<Transaction>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM transactions WHERE type = ?", arrayOf(type))
        while (cursor.moveToNext()) {
            list.add(
                Transaction(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getString(3),
                    cursor.getString(4)
                )
            )
        }
        cursor.close()
        return list
    }

    fun getTransactionsByCategory(type: String, category: String): List<Transaction> {
        val list = mutableListOf<Transaction>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM transactions WHERE type = ? AND category = ?", arrayOf(type, category))
        while (cursor.moveToNext()) {
            list.add(
                Transaction(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getString(3),
                    cursor.getString(4)
                )
            )
        }
        cursor.close()
        return list
    }
    // Удаление одной операции по ID
    fun deleteTransaction(id: Int) {
        val db = writableDatabase
        db.delete("transactions", "id = ?", arrayOf(id.toString()))
        db.close()
    }

    // Удаление всех операций в категории
    fun deleteCategory(type: String, category: String) {
        val db = writableDatabase
        db.delete("transactions", "type = ? AND category = ?", arrayOf(type, category))
        db.close()
    }
}
