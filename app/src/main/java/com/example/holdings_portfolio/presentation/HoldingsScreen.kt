package com.example.holdings_portfolio.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.holdings_portfolio.ui.theme.PortfolioBlue
import com.example.holdings_portfolio.ui.theme.PortfolioGreen
import com.example.holdings_portfolio.ui.theme.PortfolioRed
import com.example.holdings_portfolio.ui.theme.SurfaceBg
import org.koin.androidx.compose.koinViewModel

@Composable
fun HoldingsScreen(viewModel: HoldingsViewModel = koinViewModel()) {
    val holdings by viewModel.holdings.collectAsState()
    val isSummaryExpanded = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                onFilter = {},
                onProfile = {}
            )
        },
        containerColor = SurfaceBg
    ) { paddingValues ->
        Box(Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            Column(Modifier.fillMaxSize()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PortfolioBlue)
                        .padding(vertical = 24.dp)
                ) {
                    Text(
                        text = "Holdings",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(Modifier.height(8.dp))

                LazyColumn {
                    items(holdings) { holding ->
                        HoldingRow(
                            symbol = holding.symbol,
                            ltp = holding.ltp,
                            netQty = holding.quantity,
                            pnl = holding.pnl,
                            isT1 = holding.symbol.contains(
                                "T1",
                                ignoreCase = true
                            )
                        )
                        Divider(
                            thickness = 1.dp,
                            color = Color(0xFFE5E5E5)
                        )
                    }
                }

            }

            AnimatedVisibility(
                visible = isSummaryExpanded.value,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                Card(
                    Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White),
                    elevation = CardDefaults.cardElevation(12.dp),
                ) {

                    val totalInvestment = viewModel.calculateTotalInvestment(holdings)
                    val totalPnL = viewModel.calculateTotalPnL(holdings)
                    val pnlPercent = viewModel.calculatePnLPercent(totalPnL, totalInvestment)
                    val todayPnL = viewModel.calculateTodaysPnL(holdings)
                    val totalValue = viewModel.calculateCurrentValue(holdings)

                    Column(
                        Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                    ) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                buildAnnotatedString {
                                    append("Current Value")
                                    withStyle(
                                        SpanStyle(
                                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                            baselineShift = BaselineShift.Superscript,
                                        )
                                    ) { append("*") }
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                totalValue.toDisplayCurrency(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }

                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                buildAnnotatedString {
                                    append("Total Investment")
                                    withStyle(
                                        SpanStyle(
                                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                            baselineShift = BaselineShift.Superscript
                                        )
                                    ) { append("*") }
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                totalInvestment.toDisplayCurrency(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }

                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                buildAnnotatedString {
                                    append("Today's P&L")
                                    withStyle(
                                        SpanStyle(
                                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                            baselineShift = BaselineShift.Superscript
                                        )
                                    ) { append("*") }
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                todayPnL.toDisplayCurrency(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (todayPnL >= 0) PortfolioGreen else PortfolioRed,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }

                        Divider(
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                            thickness = 1.dp,
                            color = Color.Black
                        )

                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                buildAnnotatedString {
                                    append("Total P&L")
                                    withStyle(
                                        SpanStyle(
                                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                                            baselineShift = BaselineShift.Superscript
                                        )
                                    ) { append("*") }
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "${totalPnL.toDisplayCurrency()} (${pnlPercent.toDisplayPercent()})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (totalPnL >= 0) PortfolioGreen else PortfolioRed,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }

                }
            }

            FloatingActionButton(
                onClick = { isSummaryExpanded.value = !(isSummaryExpanded.value) },
                containerColor = PortfolioBlue,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (isSummaryExpanded.value) 128.dp else 32.dp)
            ) {
                Icon(
                    imageVector = if (isSummaryExpanded.value) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = "Summary"
                )
            }
        }
    }
}

@Composable
fun HoldingRow(
    symbol: String,
    ltp: Double,
    netQty: Int,
    pnl: Double,
    isT1: Boolean = false
) {
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
                    withStyle(style = SpanStyle(color = Color(0xFF8F98A9))) {
                        append("NET QTY: ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.Normal
                        )
                    ) {
                        append(netQty.toString())
                    }
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF8F98A9))) {
                        append("LTP: ")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color.Black,
                            fontWeight = FontWeight.Normal
                        )
                    ) {
                        append(ltp.toDisplayCurrency())
                    }
                },
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(24.dp))
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color(0xFF8F98A9))) {
                        append("P&L: ")
                    }
                    withStyle(style = SpanStyle(color = pnlColor, fontWeight = FontWeight.Normal)) {
                        append(formattedPnl)
                    }
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(onFilter: () -> Unit, onProfile: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Portfolio",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
            )
        },
        actions = {
            IconButton(onClick = onFilter) {
                Icon(Icons.Default.Notifications, contentDescription = "Filter", tint = Color.White)
            }
            IconButton(onClick = onProfile) {
                Box(
                    Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, "Profile", tint = Color.White)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PortfolioBlue
        )
    )
}
