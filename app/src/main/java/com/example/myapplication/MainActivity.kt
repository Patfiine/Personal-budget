package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper
    lateinit var balanceText: TextView
    lateinit var incomeChart: PieChart
    lateinit var expenseChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = DatabaseHelper(this)

        val amountInput = findViewById<EditText>(R.id.amountInput)
        val categoryInput = findViewById<EditText>(R.id.categoryInput)

        categoryInput.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

        val addIncome = findViewById<Button>(R.id.addIncome)
        val addExpense = findViewById<Button>(R.id.addExpense)

        val historyIncome = findViewById<Button>(R.id.historyIncome)
        val historyExpense = findViewById<Button>(R.id.historyExpense)

        balanceText = findViewById(R.id.balanceText)
        incomeChart = findViewById(R.id.incomeChart)
        expenseChart = findViewById(R.id.expenseChart)

        updateUI()

        addIncome.setOnClickListener {

            val amount = amountInput.text.toString().toDoubleOrNull()
            val category = categoryInput.text.toString()

            if (amount == null || category.isEmpty()) {
                Toast.makeText(this, "Введите сумму и категорию", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())

            db.addTransaction("income", amount, category, date)

            amountInput.text.clear()
            categoryInput.text.clear()

            updateUI()
        }

        addExpense.setOnClickListener {

            val amount = amountInput.text.toString().toDoubleOrNull()
            val category = categoryInput.text.toString()

            if (amount == null || category.isEmpty()) {
                Toast.makeText(this, "Введите сумму и категорию", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val date = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())

            db.addTransaction("expense", amount, category, date)

            amountInput.text.clear()
            categoryInput.text.clear()

            updateUI()
        }

        historyIncome.setOnClickListener {

            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("TYPE", "income")
            startActivity(intent)
        }

        historyExpense.setOnClickListener {

            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("TYPE", "expense")
            startActivity(intent)
        }
    }

    private fun updateUI() {

        val transactions = db.getAllTransactions()

        var balance = 0.0

        val incomeMap = mutableMapOf<String, Double>()
        val expenseMap = mutableMapOf<String, Double>()

        for (t in transactions) {

            if (t.type == "income") {

                balance += t.amount
                incomeMap[t.category] =
                    incomeMap.getOrDefault(t.category, 0.0) + t.amount

            } else {

                balance -= t.amount
                expenseMap[t.category] =
                    expenseMap.getOrDefault(t.category, 0.0) + t.amount
            }
        }

        balanceText.text = "Баланс: $balance ₽"

        drawChart(incomeChart, incomeMap, "Доходы")
        drawChart(expenseChart, expenseMap, "Расходы")
    }

    private fun drawChart(chart: PieChart, dataMap: Map<String, Double>, label: String) {

        val entries = mutableListOf<PieEntry>()

        for (item in dataMap) {
            entries.add(PieEntry(item.value.toFloat(), item.key))
        }

        val dataSet = PieDataSet(entries, label)

        val colors = ArrayList<Int>()

        colors.add(android.graphics.Color.rgb(244,67,54))
        colors.add(android.graphics.Color.rgb(33,150,243))
        colors.add(android.graphics.Color.rgb(76,175,80))
        colors.add(android.graphics.Color.rgb(255,193,7))
        colors.add(android.graphics.Color.rgb(156,39,176))
        colors.add(android.graphics.Color.rgb(0,188,212))
        colors.add(android.graphics.Color.rgb(255,87,34))

        dataSet.colors = colors

        val data = PieData(dataSet)

        data.setValueTextSize(14f)

        chart.data = data
        chart.description.isEnabled = false
        chart.invalidate()
        chart.setUsePercentValues(true)
        chart.setEntryLabelTextSize(12f)
        chart.setEntryLabelColor(android.graphics.Color.BLACK)
        chart.animateY(1000)
    }
}