package com.example.holdings_portfolio.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.holdings_portfolio.ui.theme.PortfolioBlue
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
        Box(Modifier.fillMaxSize().padding(paddingValues)) {
            Column(Modifier.fillMaxSize()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PortfolioBlue)
                        .padding(vertical = 24.dp)
                ) {
                    Text(
                        text = "Portfolio",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Holdings
                Text(
                    text = "Holdings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 8.dp)
                ) {
                    items(holdings) { holding ->
                        Card(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(holding.symbol, fontWeight = FontWeight.Bold)
                                Text("LTP: ${holding.ltp.toDisplayCurrency()}")
                                Text("Net Qty: ${holding.quantity}")
                                Text("P&L: ${holding.pnl.toDisplayCurrency()}")
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isSummaryExpanded.value,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Card(
                    Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 120.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White),
                    elevation = CardDefaults.cardElevation(12.dp),
                ) {
                    Column(
                        Modifier
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        val totalInvestment = viewModel.calculateTotalInvestment(holdings)
                        val totalPnL = viewModel.calculateTotalPnL(holdings)
                        val pnlPercent = viewModel.calculatePnLPercent(totalPnL, totalInvestment)
                        val todayPnL = viewModel.calculateTodaysPnL(holdings)
                        val totalValue = viewModel.calculateCurrentValue(holdings)

                        Text("Summary", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(12.dp))
                        Text("Current Value: ${totalValue.toDisplayCurrency()}")
                        Text("Total Investment: ${totalInvestment.toDisplayCurrency()}")
                        Text("Total P&L: ${totalPnL.toDisplayCurrency()} (${pnlPercent.toDisplayPercent()})")
                        Text("Today's P&L: ${todayPnL.toDisplayCurrency()}")
                    }
                }
            }

            FloatingActionButton(
                onClick = { isSummaryExpanded.value = !(isSummaryExpanded.value) },
                containerColor = PortfolioBlue,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (isSummaryExpanded.value) 152.dp else 32.dp)
            ) {
                Icon(
                    imageVector = if (isSummaryExpanded.value) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = "Summary"
                )
            }
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
                Icon(Icons.Default.AddCircle, contentDescription = "Filter", tint = Color.White)
            }
            IconButton(onClick = onProfile) {
                Box(
                    Modifier.size(32.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)),
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
