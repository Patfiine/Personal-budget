package com.example.myapplication

data class Transaction(
    val id: Int,
    val type: String,
    val amount: Double,
    val category: String,
    val date: String
)