package com.dsb.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InfusionDao {
    @Query("SELECT * FROM infusions ORDER BY date DESC")
    fun getAll(): Flow<List<Infusion>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM infusions")
    fun getTotalInfused(): Flow<Double>

    @Insert
    suspend fun insert(infusion: Infusion)

    @Query("DELETE FROM infusions WHERE id = :id")
    suspend fun deleteById(id: Long)
}
