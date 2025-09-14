package com.example.holdings_portfolio

import com.example.holdings_portfolio.data.api.ApiService
import com.example.holdings_portfolio.data.local.HoldingsDao
import com.example.holdings_portfolio.data.model.DataWrapper
import com.example.holdings_portfolio.data.model.Holding
import com.example.holdings_portfolio.data.model.HoldingsResponse
import com.example.holdings_portfolio.data.repository.HoldingsRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class HoldingsRepositoryImplTest {

    @Test
    fun `test getHoldings from api`() {
        runBlocking {

        val apiService: ApiService = mockk()
        val dao: HoldingsDao = mockk()
        val holding = Holding("TEST", 1, 100.0, 90.0, 100.0)
        val testList = listOf(holding)
        val response = HoldingsResponse(DataWrapper(userHolding = testList))

        coEvery { apiService.getHoldings() } returns response
        coEvery { dao.insertAll(any()) } returns Unit
        coEvery { dao.getAll() } returns emptyList() // if called (fallback)

        val repo = HoldingsRepositoryImpl(apiService, dao)
        val result = repo.getHoldings().first()

        assertEquals(1, result.size)
        assertEquals("TEST", result[0].symbol)
    }
}
}