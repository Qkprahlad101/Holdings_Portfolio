package com.example.holdings_portfolio.data.model

import com.example.holdingsapp.data.local.HoldingEntity
import com.example.holdingsapp.domain.model.DomainHolding

fun Holding.toDomain(): DomainHolding = DomainHolding(
    symbol = symbol,
    quantity = quantity,
    ltp = ltp,
    avgPrice = avgPrice,
    close = close
)

fun DomainHolding.toEntity(): HoldingEntity = HoldingEntity(
    symbol = symbol,
    quantity = quantity,
    ltp = ltp,
    avgPrice = avgPrice,
    close = close
)

fun HoldingEntity.toDomain(): DomainHolding = DomainHolding(
    symbol = symbol,
    quantity = quantity,
    ltp = ltp,
    avgPrice = avgPrice,
    close = close
)