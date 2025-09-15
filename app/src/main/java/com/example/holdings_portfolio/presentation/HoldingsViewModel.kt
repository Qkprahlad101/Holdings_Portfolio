package com.example.holdings_portfolio.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.holdings_portfolio.domain.model.DomainHolding
import com.example.holdings_portfolio.domain.usecase.GetHoldingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class HoldingsViewModel(private val getHoldingsUseCase: GetHoldingsUseCase) : ViewModel() {
    private val _holdings = MutableStateFlow<List<DomainHolding>>(emptyList())
    val holdings: StateFlow<List<DomainHolding>> = _holdings

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadHoldings()
    }

    /**
     * Public function to refresh the holdings data
     * Can be called from UI to manually trigger a refresh
     */
    fun refresh() {
        loadHoldings()
    }

    private fun loadHoldings() {
        viewModelScope.launch {
            _isLoading.value = true
            getHoldingsUseCase()
                .onStart { _error.value = null }
                .catch { e ->
                    _error.value = e.message ?: "Unknown error"
                    _holdings.value = emptyList()
                }
                .collect { data ->
                    _holdings.value = data
                }
            _isLoading.value = false
        }
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