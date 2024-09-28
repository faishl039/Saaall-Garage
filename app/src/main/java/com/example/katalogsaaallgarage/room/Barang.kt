package com.example.katalogsaaallgarage.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Barang (
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val desc: String
)