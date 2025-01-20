package com.example.resepin2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.resepin2.DAO.Recipe
import com.example.resepin2.DAO.RecipeDatabase
import com.example.resepin2.databinding.ActivityAddRecipeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddRecipeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddRecipeBinding
    private lateinit var database: RecipeDatabase
    private var imageUri: Uri? = null
    private var imagePath: String? = null

    // Launcher untuk memilih gambar
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            imageUri = selectedUri

            // Tampilkan gambar menggunakan Glide
            Glide.with(this)
                .load(selectedUri)
                .into(binding.imageView)

            // Simpan gambar ke penyimpanan internal
            lifecycleScope.launch {
                imagePath = saveImageToInternalStorage(selectedUri)
            }
        }
    }

    // Launcher untuk meminta izin
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openImagePicker()
        } else {
            Toast.makeText(this, "Izin diperlukan untuk memilih gambar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi database
        database = RecipeDatabase.getDatabase(this)

        setupViews()
    }

    private fun setupViews() {
        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Tambah Resep Baru"
        }

        // Setup tombol pilih gambar
        binding.btnSelectImage.setOnClickListener {
            checkAndRequestPermission()
        }

        // Setup tombol simpan
        binding.btnSubmit.setOnClickListener {
            saveRecipe()
        }
    }

    private fun checkAndRequestPermission() {
        val permission = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ->
                Manifest.permission.READ_MEDIA_IMAGES
            else ->
                Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Izin sudah diberikan, buka galeri
                openImagePicker()
            }
            else -> {
                // Meminta izin
                permissionLauncher.launch(permission)
            }
        }
    }

    private fun openImagePicker() {
        imagePickerLauncher.launch("image/*")
    }

    private suspend fun saveImageToInternalStorage(uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Buat nama file unik
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                val imageFileName = "JPEG_${timeStamp}_"

                // Buat direktori penyimpanan
                val storageDir = getExternalFilesDir(null)
                val image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
                )

                // Salin gambar dari URI ke file
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                val outputStream = FileOutputStream(image)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                // Kembalikan path absolut
                image.absolutePath
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun validateInput(): Boolean {
        val name = binding.edtName.text.toString().trim()
        val description = binding.edtDescription.text.toString().trim()
        val ingredients = binding.edtIngredients.text.toString().trim()
        val instructions = binding.edtInstructions.text.toString().trim()

        var isValid = true

        when {
            name.isEmpty() -> {
                binding.edtName.error = "Nama resep harus diisi"
                isValid = false
            }
            description.isEmpty() -> {
                binding.edtDescription.error = "Deskripsi harus diisi"
                isValid = false
            }
            ingredients.isEmpty() -> {
                binding.edtIngredients.error = "Bahan-bahan harus diisi"
                isValid = false
            }
            instructions.isEmpty() -> {
                binding.edtInstructions.error = "Cara membuat harus diisi"
                isValid = false
            }
        }

        return isValid
    }

    private fun saveRecipe() {
        if (validateInput()) {
            lifecycleScope.launch {
                try {
                    val recipe = Recipe(
                        name = binding.edtName.text.toString().trim(),
                        description = binding.edtDescription.text.toString().trim(),
                        ingredients = binding.edtIngredients.text.toString().trim(),
                        instructions = binding.edtInstructions.text.toString().trim(),
                        imageUrl = imagePath ?: "", // Simpan path gambar
                        isFavorite = false
                    )

                    // Simpan ke database
                    database.recipeDao().insertRecipe(recipe)

                    // Beri konfirmasi
                    Toast.makeText(
                        this@AddRecipeActivity,
                        "Resep berhasil disimpan",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Kembali ke halaman utama
                    finish()
                } catch (e: Exception) {
                    // Tangani kesalahan penyimpanan
                    Toast.makeText(
                        this@AddRecipeActivity,
                        "Gagal menyimpan resep: ${e.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // Menangani tombol back pada toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "AddRecipeActivity"
    }
}