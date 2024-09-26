package com.example.katalogsaaallgarage

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Detail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        val foto = intent.getIntExtra("foto", 0)
        val nama = intent.getStringExtra("nama")
        val descbrg = intent.getStringExtra("desc")

        findViewById<ImageView>(R.id.detail_foto).setImageResource(foto)
        findViewById<TextView>(R.id.detail_nama_barang).text = nama
        findViewById<TextView>(R.id.detail_desc_barang).text = descbrg

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}