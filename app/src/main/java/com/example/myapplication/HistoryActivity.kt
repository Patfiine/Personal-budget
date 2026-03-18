package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class HistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val listView = ListView(this)
        setContentView(listView)

        val db = DatabaseHelper(this)

        val type = intent.getStringExtra("TYPE") ?: return

        val transactions = db.getTransactionsByType(type)

        if (transactions.isEmpty()) {
            Toast.makeText(this, "Нет данных", Toast.LENGTH_SHORT).show()
        }

        val categories = transactions.map { it.category }.distinct()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            categories
        )

        listView.adapter = adapter
        // Долгое нажатие для удаления категории
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val categoryToDelete = categories[position]

            // Предупреждение
            android.app.AlertDialog.Builder(this)
                .setTitle("Удалить категорию?")
                .setMessage("Удаление категории '$categoryToDelete' приведет к удалению всех операций внутри. Продолжить?")
                .setPositiveButton("Да") { _, _ ->
                    db.deleteCategory(type, categoryToDelete)
                    Toast.makeText(this, "Категория удалена", Toast.LENGTH_SHORT).show()
                    // Обновляем список категорий
                    val updatedTransactions = db.getTransactionsByType(type)
                    val updatedCategories = updatedTransactions.map { it.category }.distinct()
                    listView.adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, updatedCategories)
                }
                .setNegativeButton("Нет", null)
                .show()

            true // чтобы событие long click было обработано
        }

        listView.setOnItemClickListener { _, _, position, _ ->

            val intent = Intent(this, TransactionListActivity::class.java)

            intent.putExtra("TYPE", type)
            intent.putExtra("CATEGORY", categories[position])

            startActivity(intent)
        }
    }

}