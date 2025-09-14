package com.example.holdings_portfolio.presentation

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

fun Double.toDisplayCurrency(): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN")) as DecimalFormat
    formatter.maximumFractionDigits = 2
    formatter.minimumFractionDigits = 2
    formatter.positivePrefix = "₹"
    formatter.negativePrefix = "-₹"
    return formatter.format(this)
}

fun Double.toDisplayPercent(): String {
    return String.format(Locale.ENGLISH, "%.2f", this) + "%"
}