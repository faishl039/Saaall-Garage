package com.example.katalogsaaallgarage

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQ_CODE_PERM)
        } else {
//            accessGallery()
        }

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
                intentEdit(Constant.TYPE_READ, barang.id) // Membuka EditActivity dengan mode baca
            }

            override fun onUpdate(barang: Barang) {
                intentEdit(Constant.TYPE_UPDATE, barang.id) // Membuka EditActivity dengan mode update
            }

            override fun onDelete(barang: Barang) {
                deleteAlert(barang) // Menampilkan dialog konfirmasi hapus
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
                    db.barangDao().deleteBarang(barang) // Menghapus barang dari database
                    dialogInterface.dismiss()
                    loadData() // Memuat ulang data setelah penghapusan
                }
            }
        }
        dialog.show()
    }


    private fun createKatalog() {
        binding.buttonCreate.setOnClickListener {
            startActivity(Intent(this, EditActivity::class.java).apply {
                putExtra("barang_id", 0) // Atau nilai default lainnya
                putExtra("intent_type", Constant.TYPE_CREATE) // Pastikan untuk mendefinisikan Constant.TYPE_CREATE
            })
        }

    }

    private fun loadData() {
        CoroutineScope(Dispatchers.IO).launch {
            val getBarang = db.barangDao().getBarang() // Memuat data dari database
            withContext(Dispatchers.Main) {
                katalogAdapter.setData(getBarang) // Mengupdate adapter dengan data yang diambil
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_CODE_PERM) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Izin diberikan, akses galeri
//                accessGallery()
            } else {
                // Izin ditolak, tampilkan pesan kepada pengguna
            }
        }
    }

    companion object {
        private const val REQ_CODE_PERM = 123
    }

}