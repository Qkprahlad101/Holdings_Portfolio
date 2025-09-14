package com.example.holdings_portfolio.data.model

import com.example.holdings_portfolio.data.local.HoldingEntity
import com.example.holdings_portfolio.domain.model.DomainHolding

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