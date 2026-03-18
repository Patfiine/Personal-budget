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

        val transactions = db.getTransactionsByCategory(type, category)

        val list = transactions.map {

            "${it.date}  ${it.amount} ₽"
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            list
        )

        listView.adapter = adapter
    }
}