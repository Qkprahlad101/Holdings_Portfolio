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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.holdings_portfolio.ui.theme.PortfolioBlue
import com.example.holdings_portfolio.ui.theme.PortfolioGreen
import com.example.holdings_portfolio.ui.theme.PortfolioRed
import com.example.holdings_portfolio.ui.theme.SurfaceBg
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
    val holdings by viewModel.holdings.collectAsState()
    val isSummaryExpanded = remember { mutableStateOf(false) }

    Scaffold(
        topBar = { SimpleAppBar() },
        containerColor = SurfaceBg
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(Modifier.fillMaxSize()) {
                LazyColumn {
                    items(holdings) { holding ->
                        HoldingRow(
                            symbol = holding.symbol,
                            ltp = holding.ltp,
                            netQty = holding.quantity,
                            pnl = holding.pnl,
                            isT1 = holding.symbol.contains("T1", ignoreCase = true)
                        )
                        Divider(thickness = 1.dp, color = Color(0xFFE5E5E5))
                    }
                }
            }

            // --- Bottom Bar: Collapsed/Expanded Summary ---
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                AnimatedContent(
                    targetState = isSummaryExpanded.value,
                    transitionSpec = {
                        fadeIn() + slideInVertically() with fadeOut() + slideOutVertically()
                    }
                ) { expanded ->
                    if (expanded) {
                        FullSummarySection(
                            holdings = holdings,
                            viewModel = viewModel,
                            onCollapse = { isSummaryExpanded.value = false }
                        )
                    } else {
                        CollapsedSummarySection(
                            holdings = holdings,
                            viewModel = viewModel,
                            onExpand = { isSummaryExpanded.value = true }
                        )
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
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Profit & Loss*", style = MaterialTheme.typography.titleMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = totalPnL.toDisplayCurrency(),
                color = pnlColor,
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = onExpand) {
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Expand Summary")
            }
        }
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
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = onCollapse) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Collapse Summary")
            }
        }

        Spacer(Modifier.height(8.dp))

        SummaryRowWithSuper("Current Value", totalValue.toDisplayCurrency())
        SummaryRowWithSuper("Total Investment", totalInvestment.toDisplayCurrency())
        SummaryRowWithSuper("Today's Profit & Loss", todayPnL.toDisplayCurrency(),
            valueColor = if (todayPnL >= 0) PortfolioGreen else PortfolioRed
        )
        Divider(Modifier.padding(vertical = 8.dp), color = Color.Black)
        SummaryRowWithSuper(
            "Profit & Loss",
            "${totalPnL.toDisplayCurrency()} (${pnlPercent.toDisplayPercent()})",
            valueColor = if (totalPnL >= 0) PortfolioGreen else PortfolioRed
        )
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
            .padding(horizontal = 12.dp, vertical = 8.dp)
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
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF8F98A9))) { append("NET QTY: ") }
                    withStyle(SpanStyle(color = Color.Black)) { append(netQty.toString()) }
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF8F98A9))) { append("LTP: ") }
                    withStyle(SpanStyle(color = Color.Black)) { append(ltp.toDisplayCurrency()) }
                },
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(24.dp))
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF8F98A9))) { append("P&L: ") }
                    withStyle(SpanStyle(color = pnlColor)) { append(formattedPnl) }
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
