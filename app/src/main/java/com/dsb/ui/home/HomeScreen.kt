package com.dsb.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dsb.data.repository.BudgetRepository
import com.dsb.ui.addexpense.AddExpenseDialog
import java.util.Locale

@Composable
fun HomeScreen(repository: BudgetRepository, showAddExpenseOnLaunch: Boolean = false) {
    val totalSpendable by repository.getTotalSpendable().collectAsState(initial = 0.0)
    val weeklyRemaining by repository.getWeeklyRemaining().collectAsState(initial = 0.0)
    val config by repository.getBudgetConfig().collectAsState(initial = null)
    var showAddExpense by remember { mutableStateOf(showAddExpenseOnLaunch) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddExpense = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Spendable",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = String.format(Locale.US, "$%.2f", totalSpendable),
                style = MaterialTheme.typography.displayLarge,
                color = if (totalSpendable >= 0) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Weekly Budget Remaining",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = String.format(Locale.US, "$%.2f", weeklyRemaining),
                style = MaterialTheme.typography.headlineLarge,
                color = if (weeklyRemaining >= 0) MaterialTheme.colorScheme.secondary
                        else MaterialTheme.colorScheme.error
            )

            config?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = String.format(Locale.US, "$%.2f / week", it.weeklyAmount),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    if (showAddExpense) {
        AddExpenseDialog(
            repository = repository,
            onDismiss = { showAddExpense = false }
        )
    }
}
