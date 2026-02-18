package com.dsb.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Goal>>

    @Query("SELECT COALESCE(SUM(fundedAmount), 0.0) FROM goals")
    fun getTotalFunded(): Flow<Double>

    @Insert
    suspend fun insert(goal: Goal)

    @Update
    suspend fun update(goal: Goal)

    @Query("DELETE FROM goals WHERE id = :id")
    suspend fun deleteById(id: Long)
}
