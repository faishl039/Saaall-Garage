package com.example.katalogsaaallgarage.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BarangDao {

    @Insert
    suspend fun addBarang(barang: Barang)

    @Update
    suspend fun updateBarang(barang: Barang)

    @Delete
    suspend fun deleteBarang(barang: Barang)

    @Query("SELECT * FROM barang")
    suspend fun getBarang(): List<Barang>

    @Query("SELECT * FROM barang WHERE id=:barang_id")
    suspend fun getIdBarang(barang_id: Int): List<Barang>
}