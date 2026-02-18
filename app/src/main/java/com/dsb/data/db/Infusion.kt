package com.dsb.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "infusions")
data class Infusion(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val note: String = "",
    val date: Long = System.currentTimeMillis()
)
