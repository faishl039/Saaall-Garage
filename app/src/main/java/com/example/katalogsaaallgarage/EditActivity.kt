package com.example.katalogsaaallgarage

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.katalogsaaallgarage.databinding.ActivityEditBinding
import com.example.katalogsaaallgarage.room.Barang
import com.example.katalogsaaallgarage.room.BarangDB
import com.example.katalogsaaallgarage.room.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBinding
    private val db by lazy { BarangDB(this) }
    private var barangId: Int = 0
    private var selectedImgUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            handleSelectedImage(it)
        } ?: run {
            Toast.makeText(this, "Gagal memilih gambar.", Toast.LENGTH_SHORT).show()
        }
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            selectImg()
        } else {
            Toast.makeText(
                this,
                "Izin ditolak. Tidak dapat mengakses penyimpanan.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSelectImage.setOnClickListener {
            selectImg()
        }
        setupView()
        setupListener()
        barangId = intent.getIntExtra("barang_id", 0)
    }

    private fun setupListener() {


        binding.buttonSave.setOnClickListener {
            addBarang()
        }

        binding.buttonUpdate.setOnClickListener {
            updateBarang()
        }
    }

    private fun selectImg() {
        pickImageLauncher.launch(arrayOf("image/*")) // Menampilkan hanya file gambar
    }


    private fun handleSelectedImage(uri: Uri) {
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            binding.imageView.setImageBitmap(bitmap)
            binding.imageView.visibility = View.VISIBLE
            selectedImgUri = uri // Simpan URI untuk digunakan nanti
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal memuat gambar.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun addBarang() {
        CoroutineScope(Dispatchers.IO).launch {
            val imgPath = selectedImgUri.toString()
            db.barangDao().addBarang(
                Barang(
                    0,
                    binding.editTitle.text.toString(),
                    binding.editDesc.text.toString(),
                    imgPath
                )
            )
            withContext(Dispatchers.Main) {
                finish()
            }
        }
    }

    private fun updateBarang() {
        CoroutineScope(Dispatchers.IO).launch {
            db.barangDao().updateBarang(
                Barang(
                    barangId,
                    binding.editTitle.text.toString(),
                    binding.editDesc.text.toString(),
                    selectedImgUri.toString()
                )
            )
            withContext(Dispatchers.Main) {
                finish()
            }
        }
    }

    private fun setupView() {
        when (intentType()) {
            Constant.TYPE_CREATE -> {
                supportActionBar!!.title = "Tambah Katalog"
                binding.buttonUpdate.visibility = View.GONE
            }

            Constant.TYPE_READ -> {
                supportActionBar!!.title = "Detail Barang"
                binding.buttonSave.visibility = View.GONE
                binding.buttonUpdate.visibility = View.GONE
                getBarang()
            }

            Constant.TYPE_UPDATE -> {
                supportActionBar!!.title = "Edit Barang"
                binding.buttonSave.visibility = View.GONE
                binding.buttonUpdate.visibility = View.VISIBLE
                getBarang()
            }
        }
    }

    private fun getBarang() {
        CoroutineScope(Dispatchers.IO).launch {
            val barang = db.barangDao().getIdBarang(barangId)
            if (barang != null && barang.isNotEmpty()) {
                val selectedBarang = barang[0]
                binding.editTitle.setText(selectedBarang.title)
                binding.editDesc.setText(selectedBarang.desc)
                selectedImgUri = Uri.parse(selectedBarang.image)
                Log.d("EditActivity", "URI gambar: $selectedImgUri")

                withContext(Dispatchers.Main) {
                    try {
                        // Pastikan Anda memeriksa jika selectedImgUri tidak null
                        selectedImgUri?.let {
                            val inputStream = contentResolver.openInputStream(it)
                            val bitmap = BitmapFactory.decodeStream(inputStream)
                            binding.imageView.setImageBitmap(bitmap)
                            binding.imageView.visibility = View.VISIBLE
                        } ?: run {
                            Toast.makeText(
                                this@EditActivity,
                                "URI gambar tidak valid.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: SecurityException) {
                        Log.e("EditActivity", "Permission Denial: ${e.message}")
                        Toast.makeText(
                            this@EditActivity,
                            "Gagal memuat gambar. Izin mungkin ditolak.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@EditActivity,
                            "Gagal memuat gambar.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Log.e("EditActivity", "Barang tidak ditemukan")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditActivity, "Barang tidak ditemukan", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
    private fun intentType(): Int {
        return intent.getIntExtra("intent_type", 0)
    }
}
