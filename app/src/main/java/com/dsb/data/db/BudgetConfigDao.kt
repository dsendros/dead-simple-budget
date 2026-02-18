package com.dsb.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetConfigDao {
    @Query("SELECT * FROM budget_config WHERE id = 1")
    fun get(): Flow<BudgetConfig?>

    @Query("SELECT * FROM budget_config WHERE id = 1")
    suspend fun getOnce(): BudgetConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(config: BudgetConfig)
}
