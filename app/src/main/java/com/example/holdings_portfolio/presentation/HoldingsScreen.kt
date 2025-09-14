package com.example.holdings_portfolio.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun HoldingsScreen(viewModel: HoldingsViewModel = koinViewModel()) {
    val holdings by viewModel.holdings.collectAsState()
    val isExpanded by viewModel.isExpanded
    val error by viewModel.error

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text(
            text = "Portfolio",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        if (error != null) {
            Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
        }

        // Summary Card (Expandable)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            onClick = { viewModel.toggleExpanded() }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Profit & Loss: ₹${viewModel.calculateTotalPnL(holdings)} (${
                        (viewModel.calculateTotalPnL(
                            holdings
                        ) / viewModel.calculateTotalInvestment(holdings) * 100).toInt()
                    }%)"
                )
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Current Value: ₹${viewModel.calculateCurrentValue(holdings)}")
                    Text("Total Investment: ₹${viewModel.calculateTotalInvestment(holdings)}")
                    Text("Today's P&L: ₹${viewModel.calculateTodaysPnL(holdings)}")
                }
            }
        }

        Text("Holdings", style = MaterialTheme.typography.titleLarge)

        LazyColumn {
            items(holdings) { holding ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${holding.symbol}")
                        Text("LTP: ₹${holding.ltp}")
                        Text("Net Qty: ${holding.quantity}")
                        Text("P&L: ₹${holding.pnl}")
                    }
                }
            }
        }
    }
}