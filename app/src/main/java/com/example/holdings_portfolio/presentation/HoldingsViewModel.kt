package com.example.holdings_portfolio.presentation


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.holdings_portfolio.domain.model.DomainHolding
import com.example.holdings_portfolio.domain.usecase.GetHoldingsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class HoldingsUiState(
    val holdings: List<DomainHolding> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
class HoldingsViewModel(private val getHoldingsUseCase: GetHoldingsUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(HoldingsUiState())
    val uiState: StateFlow<HoldingsUiState> = _uiState.asStateFlow()

    init {
        loadHoldings()
    }

    fun loadHoldings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            getHoldingsUseCase()
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true, error = null) }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "No Internet Connection",
                        holdings = emptyList()
                    )
                }
                .collect { data ->
                    _uiState.value = _uiState.value.copy(
                        holdings = data,
                        isLoading = false,
                        error = null
                    )
                }
        }
    }

    fun refresh() = loadHoldings()

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