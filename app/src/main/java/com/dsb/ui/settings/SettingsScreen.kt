package com.dsb.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.dsb.data.repository.BudgetRepository
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(repository: BudgetRepository) {
    val config by repository.getBudgetConfig().collectAsState(initial = null)
    var amountText by remember(config) {
        mutableStateOf(config?.weeklyAmount?.toString() ?: "")
    }
    var selectedDay by remember(config) {
        mutableIntStateOf(config?.startDayOfWeek ?: 1)
    }
    val amount = amountText.toDoubleOrNull()
    val scope = rememberCoroutineScope()
    var saved by remember { mutableStateOf(false) }
    var dayMenuExpanded by remember { mutableStateOf(false) }
    var showAddInfusion by remember { mutableStateOf(false) }

    val dayNames = (1..7).map { DayOfWeek.of(it).getDisplayName(TextStyle.FULL, Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
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

            ExposedDropdownMenuBox(
                expanded = dayMenuExpanded,
                onExpandedChange = { dayMenuExpanded = it }
            ) {
                OutlinedTextField(
                    value = DayOfWeek.of(selectedDay).getDisplayName(TextStyle.FULL, Locale.getDefault()),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Week starts on") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dayMenuExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = dayMenuExpanded,
                    onDismissRequest = { dayMenuExpanded = false }
                ) {
                    dayNames.forEachIndexed { index, name ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                selectedDay = index + 1
                                dayMenuExpanded = false
                                saved = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    amount?.let {
                        scope.launch {
                            repository.setBudgetConfig(it, selectedDay)
                            saved = true
                        }
                    }
                },
                enabled = amount != null && amount > 0 && !saved,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (saved) "Saved!" else "Save")
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "One-Time Infusions",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { showAddInfusion = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Infusion")
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    if (showAddInfusion) {
        AddInfusionDialog(
            repository = repository,
            onDismiss = { showAddInfusion = false }
        )
    }
}
