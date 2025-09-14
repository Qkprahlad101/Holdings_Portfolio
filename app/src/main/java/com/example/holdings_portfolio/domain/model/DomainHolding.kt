package com.example.holdings_portfolio.domain.model

data class DomainHolding(
    val symbol: String,
    val quantity: Int,
    val ltp: Double,
    val avgPrice: Double,
    val close: Double
) {
    val pnl: Double get() = (ltp - avgPrice) * quantity
}
