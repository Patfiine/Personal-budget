package com.example.myapplication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class TransactionListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val listView = ListView(this)
        setContentView(listView)

        val db = DatabaseHelper(this)

        val type = intent.getStringExtra("TYPE") ?: return
        val category = intent.getStringExtra("CATEGORY") ?: return

        var transactions = db.getTransactionsByCategory(type, category)

        if (transactions.isEmpty()) {
            Toast.makeText(this, "Нет операций", Toast.LENGTH_SHORT).show()
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            transactions.map { "${it.amount} ₽ - ${it.date}" }
        )
        listView.adapter = adapter

        // Долгое нажатие для удаления конкретной операции
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val transactionToDelete = transactions[position]

            android.app.AlertDialog.Builder(this)
                .setTitle("Удалить операцию?")
                .setMessage("Вы действительно хотите удалить эту операцию (${transactionToDelete.amount} ₽)?")
                .setPositiveButton("Да") { _, _ ->
                    db.deleteTransaction(transactionToDelete.id)
                    Toast.makeText(this, "Операция удалена", Toast.LENGTH_SHORT).show()
                    // Обновляем список после удаления
                    transactions = db.getTransactionsByCategory(type, category)
                    listView.adapter = ArrayAdapter(
                        this,
                        android.R.layout.simple_list_item_1,
                        transactions.map { "${it.amount} ₽ - ${it.date}" }
                    )
                }
                .setNegativeButton("Нет", null)
                .show()

            true
        }
    }
}