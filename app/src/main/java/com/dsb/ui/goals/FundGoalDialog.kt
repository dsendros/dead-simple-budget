package com.dsb.ui.goals

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dsb.data.db.Goal
import com.dsb.data.repository.BudgetRepository
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun FundGoalDialog(goal: Goal, repository: BudgetRepository, onDismiss: () -> Unit) {
    var amountText by remember { mutableStateOf("") }
    val amount = amountText.toDoubleOrNull()
    val remaining = goal.targetAmount - goal.fundedAmount
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Fund: ${goal.title}") },
        text = {
            Column {
                LinearProgressIndicator(
                    progress = { (goal.fundedAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = String.format(Locale.US, "$%.2f / $%.2f", goal.fundedAmount, goal.targetAmount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = String.format(Locale.US, "$%.2f remaining", remaining),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount to fund") },
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    amount?.let {
                        scope.launch {
                            repository.fundGoal(goal, it)
                            onDismiss()
                        }
                    }
                },
                enabled = amount != null && amount > 0 && amount <= remaining
            ) {
                Text("Fund")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
