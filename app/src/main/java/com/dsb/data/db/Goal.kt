package com.dsb.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetAmount: Double,
    val fundedAmount: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)
