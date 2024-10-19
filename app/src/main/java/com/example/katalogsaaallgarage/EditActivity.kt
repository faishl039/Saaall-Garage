package com.example.katalogsaaallgarage

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSave.isEnabled = false
        binding.buttonUpdate.isEnabled = false

        setupView()
        setupListener()
        barangId = intent.getIntExtra("barang_id", 0)
    }

    private fun setupListener() {

        val editTexts = listOf(
            binding.editTitle,
            binding.editDesc,
            binding.editStock,
            binding.editPrice
        )

        // TextWatcher untuk memantau perubahan pada semua EditText
        for (editText in editTexts) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    validateFields()  // Panggil fungsi untuk memvalidasi field
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        binding.buttonSave.setOnClickListener {
            addBarang()
        }

        binding.buttonUpdate.setOnClickListener {
            updateBarang()
        }
    }

    private fun validateFields() {
        val title = binding.editTitle.text.toString()
        val desc = binding.editDesc.text.toString()
        val stock = binding.editStock.text.toString()
        val priceStr = binding.editPrice.text.toString()

        // Reset error di TextInputLayout harga
        binding.inplayprice.error = null

        val isAllFieldsFilled = title.isNotEmpty() &&
                desc.isNotEmpty() &&
                stock.isNotEmpty() &&
                priceStr.isNotEmpty()

        // Cek apakah harga kurang dari 1000
        val price = priceStr.toIntOrNull()
        if (price != null && price < 1000) {
            binding.inplayprice.error = "Tidak ada harga barang dibawah 1000"
            binding.buttonSave.isEnabled = false
            binding.buttonUpdate.isEnabled = false
            return
        }

        // Aktifkan button jika semua field terisi dan harga valid
        binding.buttonSave.isEnabled = isAllFieldsFilled && price != null && price >= 1000
        binding.buttonUpdate.isEnabled = isAllFieldsFilled && price != null && price >= 1000
    }



    private fun addBarang() {
        CoroutineScope(Dispatchers.IO).launch {
            db.barangDao().addBarang(
                Barang(
                    0,
                    binding.editTitle.text.toString(),
                    binding.editDesc.text.toString(),
                    binding.editStock.text.toString().toInt(),
                    binding.editPrice.text.toString(),
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
                    binding.editStock.text.toString().toInt(),
                    binding.editPrice.text.toString(),
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
                withContext(Dispatchers.Main) {
                    binding.editTitle.setText(selectedBarang.title)
                    binding.editDesc.setText(selectedBarang.desc)
                    binding.editStock.setText(selectedBarang.stock.toString())
                    binding.editPrice.setText(selectedBarang.price)
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
