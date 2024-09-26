package com.example.katalogsaaallgarage

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), ListKatalogAdapter.OnItemClickListener {
    private lateinit var tampilKatalog: RecyclerView
    private val list = ArrayList<Katalog>()
    private lateinit var listKatalogAdapter: ListKatalogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        tampilKatalog = findViewById(R.id.list_katalog)
        tampilKatalog.setHasFixedSize(true)

        list.addAll(getListKatalog())
        showRecyclerList()

        listKatalogAdapter = ListKatalogAdapter(list, this)
        tampilKatalog.adapter = listKatalogAdapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showRecyclerList() {
        tampilKatalog.layoutManager = LinearLayoutManager(this)
//        val listKatalogAdapter = ListKatalogAdapter(list)
//        tampilKatalog.adapter = listKatalogAdapter
    }

    private fun getListKatalog(): ArrayList<Katalog> {
        val dataNama = resources.getStringArray(R.array.list_katalog_barang)
        val dataDesc = resources.getStringArray(R.array.desc_katalog_barang)
        val dataFoto = resources.obtainTypedArray(R.array.foto_katalog_barang)
        val listKatalog = ArrayList<Katalog>()
        for (i in dataNama.indices) {
            val barang = Katalog(dataNama[i], dataDesc[i], dataFoto.getResourceId(i, -1))
            listKatalog.add(barang)
        }
        return listKatalog
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.to_profile, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about_page -> {
                val moveKeAboutPage = Intent(this@MainActivity, AboutPage::class.java)
                startActivity(moveKeAboutPage)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClick(item: Katalog) {
        val intent = Intent(this, Detail::class.java).apply {
            putExtra("foto", item.foto)
            putExtra("nama", item.nama)
            putExtra("desc", item.desc)
        }
        startActivity(intent)
    }
}