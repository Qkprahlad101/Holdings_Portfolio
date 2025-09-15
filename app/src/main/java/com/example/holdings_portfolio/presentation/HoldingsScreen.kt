package com.example.holdings_portfolio.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.holdings_portfolio.domain.model.DomainHolding
import com.example.holdings_portfolio.ui.theme.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleAppBar() {
    TopAppBar(
        title = {
            Text(
                text = "Holdings",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = PortfolioBlue)
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HoldingsScreen(viewModel: HoldingsViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing = uiState.isLoading
    val summaryExpanded = remember { mutableStateOf(false) }

    Scaffold(
        topBar = { SimpleAppBar() },
    ) { paddingValues ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = { viewModel.loadHoldings() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(Modifier.fillMaxSize()) {
                when {
                    isRefreshing && uiState.holdings.isEmpty() -> {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .height(600.dp),
                            contentAlignment = Alignment.Center
                        ) {}
                    }
                    uiState.error != null -> {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .height(600.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.error ?: "Error",
                                color = Color.Red,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    uiState.holdings.isEmpty() -> {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .height(600.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No holdings found",
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                    else -> {
                        Column(Modifier.fillMaxSize()) {
                            LazyColumn {
                                items(uiState.holdings) { holding ->
                                    HoldingRow(
                                        symbol = holding.symbol,
                                        netQty = holding.quantity,
                                        ltp = holding.ltp,
                                        pnl = holding.pnl,
                                        isT1 = holding.symbol.contains("T1", ignoreCase = true)
                                    )
                                    Divider(color = Color.LightGray, thickness = 1.dp)
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .background(Color.LightGray)
                        ) {
                            AnimatedContent(
                                targetState = summaryExpanded.value,
                                transitionSpec = {
                                    fadeIn() + slideInVertically() with fadeOut() + slideOutVertically()
                                }
                            ) { expanded ->
                                if (uiState.error == null) {
                                    if (expanded) {
                                        FullSummarySection(
                                            holdings = uiState.holdings,
                                            viewModel = viewModel,
                                            onCollapse = { summaryExpanded.value = false }
                                        )
                                    } else {
                                        CollapsedSummarySection(
                                            holdings = uiState.holdings,
                                            viewModel = viewModel,
                                            onExpand = { summaryExpanded.value = true }
                                        )
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun CollapsedSummarySection(
    holdings: List<DomainHolding>,
    viewModel: HoldingsViewModel,
    onExpand: () -> Unit
) {
    val totalPnL = viewModel.calculateTotalPnL(holdings)
    val pnlColor = if (totalPnL >= 0) PortfolioGreen else PortfolioRed

    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                buildAnnotatedString {
                    append("Profit & Loss")
                    withStyle(
                        style = SpanStyle(
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            baselineShift = BaselineShift.Superscript
                        )
                    ) { append("*") }
                },
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onExpand) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Expand Summary")
            }
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = totalPnL.toDisplayCurrency(),
            color = pnlColor,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.End
        )
    }
}


@Composable
fun FullSummarySection(
    holdings: List<DomainHolding>,
    viewModel: HoldingsViewModel,
    onCollapse: () -> Unit
) {
    val totalInvestment = viewModel.calculateTotalInvestment(holdings)
    val totalPnL = viewModel.calculateTotalPnL(holdings)
    val pnlPercent = viewModel.calculatePnLPercent(totalPnL, totalInvestment)
    val todayPnL = viewModel.calculateTodaysPnL(holdings)
    val totalValue = viewModel.calculateCurrentValue(holdings)

    Column(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        SummaryRowWithSuper("Current Value", totalValue.toDisplayCurrency())
        SummaryRowWithSuper("Total Investment", totalInvestment.toDisplayCurrency())
        SummaryRowWithSuper(
            "Today's Profit & Loss",
            todayPnL.toDisplayCurrency(),
            valueColor = if (todayPnL >= 0) PortfolioGreen else PortfolioRed
        )
        Divider(Modifier.padding(vertical = 8.dp), color = Color.Black)

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    buildAnnotatedString {
                        append("Profit & Loss")
                        withStyle(
                            style = SpanStyle(
                                fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                baselineShift = BaselineShift.Superscript
                            )
                        ) { append("*") }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                IconButton(onClick = onCollapse) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Collapse Summary")
                }
            }
            Spacer(Modifier.weight(1f))
            Text(
                "${totalPnL.toDisplayCurrency()} (${pnlPercent.toDisplayPercent()})",
                style = MaterialTheme.typography.bodyMedium,
                color = if (totalPnL >= 0) PortfolioGreen else PortfolioRed,
                textAlign = TextAlign.End
            )
        }
    }
}


@Composable
fun SummaryRowWithSuper(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            buildAnnotatedString {
                append(label)
                withStyle(
                    style = SpanStyle(
                        fontSize = MaterialTheme.typography.bodySmall.fontSize,
                        baselineShift = BaselineShift.Superscript
                    )
                ) { append("*") }
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

@Composable
fun HoldingRow(symbol: String, ltp: Double, netQty: Int, pnl: Double, isT1: Boolean = false) {
    val pnlColor = if (pnl >= 0) PortfolioGreen else PortfolioRed
    val formattedPnl = pnl.toDisplayCurrency()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        Column(Modifier.weight(2f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = symbol,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black
                )
                if (isT1) {
                    Spacer(Modifier.width(4.dp))
                    Surface(
                        color = Color.LightGray,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(start = 2.dp)
                    ) {
                        Text(
                            text = "T1 Holding",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.DarkGray,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "NET QTY:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8F98A9)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = netQty.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
        }
        Spacer(Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "LTP:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8F98A9)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = ltp.toDisplayCurrency(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "P&L:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF8F98A9)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = formattedPnl,
                    style = MaterialTheme.typography.bodyMedium,
                    color = pnlColor
                )
            }
        }
    }
}
