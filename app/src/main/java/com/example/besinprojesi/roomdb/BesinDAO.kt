package com.example.besinprojesi.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.besinprojesi.model.Besin

@Dao
interface BesinDAO {

    @Insert
    suspend fun insertAll(vararg besin: Besin): List<Long>

    @Query("SELECT * FROM besin")
    suspend fun getAllBesin(): List<Besin>

    @Query("SELECT * FROM besin WHERE uuid = :besinId")
    suspend fun getBesin(besinId: Int): Besin

    @Query("DELETE FROM besin")
    suspend fun deleteAllBesin()

}