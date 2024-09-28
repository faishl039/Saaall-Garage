package com.example.katalogsaaallgarage.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Barang::class],
    version = 1
)

abstract class BarangDB : RoomDatabase() {

    abstract fun barangDao(): BarangDao

    companion object {
        @Volatile private var instance : BarangDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            BarangDB::class.java,
            "BarangJualan.db"
        ).build()
    }

}