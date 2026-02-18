package com.dsb.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_config")
data class BudgetConfig(
    @PrimaryKey val id: Int = 1,
    val weeklyAmount: Double,
    val startDate: Long = System.currentTimeMillis()
)
