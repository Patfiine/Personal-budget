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

        listView.setOnItemClickListener { _, _, position, _ ->

            val intent = Intent(this, TransactionListActivity::class.java)

            intent.putExtra("TYPE", type)
            intent.putExtra("CATEGORY", categories[position])

            startActivity(intent)
        }
    }
}