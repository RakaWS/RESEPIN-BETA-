package com.example.resepin2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.resepin2.DAO.Recipe
import com.example.resepin2.DAO.RecipeDatabase
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var recipeImage: ImageView
    private lateinit var recipeName: TextView
    private lateinit var recipeDescription: TextView
    private lateinit var favoriteButton: ImageButton
    private lateinit var editButton: MaterialButton
    private lateinit var deleteButton: MaterialButton
    private lateinit var database: RecipeDatabase
    private var recipeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        // Inisialisasi views
        recipeImage = findViewById(R.id.imgRecipeDetail)
        recipeName = findViewById(R.id.tvRecipeNameDetail)
        recipeDescription = findViewById(R.id.tvRecipeDescriptionDetail)
        favoriteButton = findViewById(R.id.btnFavoriteDetail)
        editButton = findViewById(R.id.btnEdit)
        deleteButton = findViewById(R.id.btnDelete)

        // Inisialisasi database
        database = RecipeDatabase.getDatabase(this)

        // Ambil ID resep dari intent
        recipeId = intent.getIntExtra("RECIPE_ID", -1)
        if (recipeId != -1) {
            loadRecipeDetails(recipeId)
        }

        // Setup tombol edit
        editButton.setOnClickListener {
            editRecipe()
        }

        // Setup tombol hapus
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun loadRecipeDetails(recipeId: Int) {
        lifecycleScope.launch {
            // Ambil detail resep dari database berdasarkan ID
            val recipe = database.recipeDao().getRecipeById(recipeId)
            Log.d("RECIPE", "$recipe")
            // Tampilkan data resep
            recipeName.text = recipe?.name
            recipeDescription.text = recipe?.description
            Glide.with(this@RecipeDetailActivity)
                .load(recipe?.imageUrl)
                .placeholder(R.drawable.ic_placeholder) // Gambar placeholder
                .into(recipeImage)

            // Set status favorit
            favoriteButton.setImageResource(
                if (recipe?.isFavorite == true) R.drawable.ic_favorite
                else R.drawable.ic_favorite_border
            )

            // Tambahkan aksi ketika tombol favorit diklik
            favoriteButton.setOnClickListener {
                if (recipe != null) {
                    recipe.isFavorite = !recipe.isFavorite
                    // Update status favorit di database
                    lifecycleScope.launch {
                        database.recipeDao().updateFavoriteStatus(recipe.id, recipe.isFavorite)
                    }
                    // Ubah icon favorit
                    favoriteButton.setImageResource(
                        if (recipe.isFavorite) R.drawable.ic_favorite
                        else R.drawable.ic_favorite_border
                    )
                }
            }
        }
    }

    private fun editRecipe() {
        // Pindah ke AddRecipeActivity dengan ID resep
        val intent = Intent(this, AddRecipeActivity::class.java).apply {
            putExtra("RECIPE_ID", recipeId)
            putExtra("IS_EDIT_MODE", true)
        }
        startActivity(intent)
        finish()
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Resep")
            .setMessage("Apakah Anda yakin ingin menghapus resep ini?")
            .setPositiveButton("Ya") { _, _ ->
                deleteRecipe()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun deleteRecipe() {
        lifecycleScope.launch {
            // Hapus resep dari database
            database.recipeDao().deleteRecipeById(recipeId)

            Toast.makeText(
                this@RecipeDetailActivity,
                "Resep berhasil dihapus",
                Toast.LENGTH_SHORT
            ).show()

            // Kembali ke halaman utama
            finish()
        }
    }
}
