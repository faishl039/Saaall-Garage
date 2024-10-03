package com.example.katalogsaaallgarage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.katalogsaaallgarage.databinding.ActivityMainBinding
import com.example.katalogsaaallgarage.room.Barang
import com.example.katalogsaaallgarage.room.BarangDB
import com.example.katalogsaaallgarage.room.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    val db by lazy { BarangDB(this) }
    lateinit var katalogAdapter: KatalogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        createKatalog()
        setupRV()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.IO).launch {
            val getBarang = db.barangDao().getBarang()
            Log.d("MainAct", "dbResp $getBarang")
            withContext(Dispatchers.Main) {
                katalogAdapter.setData(getBarang)
            }
        }
    }

    private fun setupRV() {
        katalogAdapter = KatalogAdapter(arrayListOf(), object : KatalogAdapter.OnAdapterListener {
            override fun onRead(barang: Barang) {
                intentEdit(Constant.TYPE_READ, barang.id)
            }

            override fun onUpdate(barang: Barang) {
                intentEdit(Constant.TYPE_UPDATE, barang.id)
            }

            override fun onDelete(barang: Barang) {
                deleteAlert(barang)
            }
        })


        binding.listKatalog.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = katalogAdapter
        }
    }

    private fun deleteAlert(barang: Barang) {
        val dialog = AlertDialog.Builder(this)
        dialog.apply {
            setTitle("Konfirmasi Hapus")
            setMessage("Yakin hapus ${barang.title}?")
            setNegativeButton("Batal") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            setPositiveButton("Hapus") { dialogInterface, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    db.barangDao().deleteBarang(barang)
                    dialogInterface.dismiss()
                    loadData()
                }
            }
        }
        dialog.show()
    }


    private fun createKatalog() {
        binding.buttonCreate.setOnClickListener {
            startActivity(Intent(this, EditActivity::class.java).apply {
                putExtra("barang_id", 0)
                putExtra("intent_type", Constant.TYPE_CREATE)
            })
        }

    }

    private fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            val getBarang = db.barangDao().getBarang()
            withContext(Dispatchers.Main) {
                katalogAdapter.setData(getBarang)
            }
        }
    }

    private fun intentEdit(intentType: Int, barangId: Int) {
        startActivity(
            Intent(this, EditActivity::class.java).apply {
                putExtra("intent_type", intentType)
                putExtra("barang_id", barangId)
            }
        )
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


}