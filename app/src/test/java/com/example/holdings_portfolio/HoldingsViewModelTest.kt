package com.example.holdings_portfolio

import com.example.holdings_portfolio.domain.model.DomainHolding
import com.example.holdings_portfolio.domain.usecase.GetHoldingsUseCase
import com.example.holdings_portfolio.presentation.HoldingsViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class HoldingsViewModelTest {

    @Test
    fun `test calculateTotalPnL`() = runTest {
        val useCase = mockk<GetHoldingsUseCase>()
        val holdings = listOf(
            DomainHolding("TEST", 1, 100.0, 90.0, 100.0)
        )
        every { useCase() } returns flowOf(holdings)

        val viewModel = HoldingsViewModel(useCase)
        assertEquals(10.0, viewModel.calculateTotalPnL(holdings), 0.0)
    }

    @Test
    fun `test calculateTodaysPnL`() = runTest {
        val useCase = mockk<GetHoldingsUseCase>()
        val holdings = listOf(
            DomainHolding("TEST", 1, 100.0, 90.0, 105.0)
        )
        every { useCase() } returns flowOf(holdings)

        val viewModel = HoldingsViewModel(useCase)
        assertEquals(5.0, viewModel.calculateTodaysPnL(holdings), 0.0)
    }
}