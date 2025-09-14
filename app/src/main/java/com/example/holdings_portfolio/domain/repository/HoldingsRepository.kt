package com.example.holdings_portfolio.domain.repository

import com.example.holdings_portfolio.domain.model.DomainHolding
import kotlinx.coroutines.flow.Flow

interface HoldingsRepository {
    fun getHoldings(): Flow<List<DomainHolding>>
}