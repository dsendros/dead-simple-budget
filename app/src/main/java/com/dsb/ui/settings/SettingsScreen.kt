package com.dsb.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dsb.data.repository.BudgetRepository
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(repository: BudgetRepository) {
    val config by repository.getBudgetConfig().collectAsState(initial = null)
    var amountText by remember(config) {
        mutableStateOf(config?.weeklyAmount?.toString() ?: "")
    }
    val amount = amountText.toDoubleOrNull()
    val scope = rememberCoroutineScope()
    var saved by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = amountText,
            onValueChange = {
                amountText = it
                saved = false
            },
            label = { Text("Weekly budget amount") },
            prefix = { Text("$") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                amount?.let {
                    scope.launch {
                        repository.setBudgetConfig(it)
                        saved = true
                    }
                }
            },
            enabled = amount != null && amount > 0 && !saved,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (saved) "Saved!" else "Save")
        }
    }
}
