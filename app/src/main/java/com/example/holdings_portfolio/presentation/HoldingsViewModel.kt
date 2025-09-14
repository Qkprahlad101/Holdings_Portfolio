package com.example.holdings_portfolio.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.holdings_portfolio.domain.model.DomainHolding
import com.example.holdings_portfolio.domain.usecase.GetHoldingsUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HoldingsViewModel(private val getHoldingsUseCase: GetHoldingsUseCase) : ViewModel() {

    val holdings: StateFlow<List<DomainHolding>> = getHoldingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isExpanded = mutableStateOf(false)
    val isExpanded: State<Boolean> = _isExpanded

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    init {
        loadHoldings()
    }

    private fun loadHoldings() {
        viewModelScope.launch {
            getHoldingsUseCase().catch { e ->
                _error.value = e.message
            }.collect { }
        }
    }

    fun toggleExpanded() {
        _isExpanded.value = !_isExpanded.value
    }

    fun calculatePnLPercent(totalPnL: Double, totalInvestment: Double): Double {
        return if (totalInvestment != 0.0) totalPnL / totalInvestment * 100 else 0.0
    }

    fun calculateCurrentValue(holdings: List<DomainHolding>): Double =
        holdings.sumOf { it.ltp * it.quantity }

    fun calculateTotalInvestment(holdings: List<DomainHolding>): Double =
        holdings.sumOf { it.avgPrice * it.quantity }

    fun calculateTotalPnL(holdings: List<DomainHolding>): Double =
        calculateCurrentValue(holdings) - calculateTotalInvestment(holdings)

    fun calculateTodaysPnL(holdings: List<DomainHolding>): Double =
        holdings.sumOf { (it.close - it.ltp) * it.quantity }
}