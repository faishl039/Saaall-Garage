package com.example.katalogsaaallgarage

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Katalog(
    val nama: String,
    val desc: String,
    val foto: Int
) : Parcelable
