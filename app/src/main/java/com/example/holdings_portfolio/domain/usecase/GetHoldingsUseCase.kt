package com.example.holdings_portfolio.domain.usecase

import com.example.holdings_portfolio.domain.repository.HoldingsRepository
import com.example.holdings_portfolio.domain.model.DomainHolding
import kotlinx.coroutines.flow.Flow

class GetHoldingsUseCase(private val repository: HoldingsRepository) {
    operator fun invoke(): Flow<List<DomainHolding>> = repository.getHoldings()
}