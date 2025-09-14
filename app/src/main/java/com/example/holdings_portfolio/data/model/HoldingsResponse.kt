package com.example.holdings_portfolio.data.model

data class HoldingsResponse(
    val data: DataWrapper
)

data class DataWrapper(
    val userHolding: List<Holding>
)

data class Holding(
    val symbol: String,
    val quantity: Int,
    val ltp: Double,
    val avgPrice: Double,
    val close: Double
)