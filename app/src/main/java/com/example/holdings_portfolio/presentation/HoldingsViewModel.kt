package com.example.holdings_portfolio.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.holdings_portfolio.domain.model.DomainHolding
import com.example.holdings_portfolio.domain.usecase.GetHoldingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadHoldings()
    }

    private fun loadHoldings() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                getHoldingsUseCase()
                    .catch { e -> _error.value = e.message ?: "No Internet Connection" }
                    .collect {
                        _error.value = null
                    }
            } catch (e: Exception) {
                _error.value = e.message ?: "No Internet Connection"
            } finally {
                _isLoading.value = false
            }
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