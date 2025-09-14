package com.example.holdings_portfolio.data.repository

import com.example.holdings_portfolio.data.api.ApiService
import com.example.holdings_portfolio.data.local.HoldingsDao
import com.example.holdings_portfolio.data.model.toDomain
import com.example.holdings_portfolio.data.model.toEntity
import com.example.holdings_portfolio.domain.model.DomainHolding
import com.example.holdings_portfolio.domain.repository.HoldingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HoldingsRepositoryImpl(
    private val apiService: ApiService,
    private val dao: HoldingsDao
) : HoldingsRepository {

    override fun getHoldings(): Flow<List<DomainHolding>> = flow {
        try {
            val response = apiService.getHoldings()
            val domainHoldings = response.data.userHolding.map { it.toDomain() }
            dao.insertAll(domainHoldings.map { it.toEntity() })
            emit(domainHoldings)
        } catch (e: Exception) {
            val cached = dao.getAll().map { it.toDomain() }
            if (cached.isNotEmpty()) {
                emit(cached)
            } else {
                throw e // Or handle error
            }
        }
    }
}