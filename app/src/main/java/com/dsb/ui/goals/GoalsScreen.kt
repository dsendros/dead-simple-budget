package com.dsb.ui.goals

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dsb.data.db.Goal
import com.dsb.data.repository.BudgetRepository
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun GoalsScreen(repository: BudgetRepository) {
    val goals by repository.getGoals().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var showAddGoal by remember { mutableStateOf(false) }
    var fundingGoal by remember { mutableStateOf<Goal?>(null) }
    var deletingGoal by remember { mutableStateOf<Goal?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddGoal = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal")
            }
        }
    ) { padding ->
        if (goals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No goals yet",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(goals, key = { it.id }) { goal ->
                    val progress = if (goal.targetAmount > 0) {
                        (goal.fundedAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
                    } else 0f

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { fundingGoal = goal }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = goal.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = String.format(
                                        Locale.US, "$%.2f / $%.2f",
                                        goal.fundedAmount, goal.targetAmount
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { deletingGoal = goal }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddGoal) {
        AddGoalDialog(
            repository = repository,
            onDismiss = { showAddGoal = false }
        )
    }

    fundingGoal?.let { goal ->
        FundGoalDialog(
            goal = goal,
            repository = repository,
            onDismiss = { fundingGoal = null }
        )
    }

    deletingGoal?.let { goal ->
        AlertDialog(
            onDismissRequest = { deletingGoal = null },
            title = { Text("Delete goal?") },
            text = { Text("Delete \"${goal.title}\"? Any funded amount will be returned to your total budget.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { repository.deleteGoal(goal.id) }
                    deletingGoal = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingGoal = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
