package com.example.resepin2
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resepin2.DAO.RecipeDatabase
import com.google.android.material.appbar.MaterialToolbar

class FavoriteActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var database: RecipeDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite) // Menghubungkan dengan layout XML

        // Inisialisasi database
        database = RecipeDatabase.getDatabase(this)

        // Setup tampilan
        setupViews()
        // Muat data resep favorit
//        loadFavoriteRecipes()
    }

    private fun setupViews() {
        // Setup toolbar dengan tombol back
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Resep Favorit"

        // Setup RecyclerView untuk menampilkan daftar resep favorit
        recyclerView = findViewById(R.id.favoriteRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Menambahkan aksi ketika resep diklik
//        recipeAdapter = RecipeAdapter(
//            onRecipeClick = { recipe ->
//                // Contoh: membuka detail resep
//                val intent = Intent(this, RecipeDetailActivity::class.java)
//                intent.putExtra("RECIPE_ID", recipe.id)
//                startActivity(intent)
//            },
//            onFavoriteClick = { recipe ->
//                // Contoh: aksi ketika tombol favorit di klik
//                lifecycleScope.launch {
//                    recipe.isFavorite = !recipe.isFavorite
//                    database.recipeDao().updateRecipe(recipe)
//                    loadFavoriteRecipes() // Muat ulang daftar favorit
//                }
//            }
//        )
        recyclerView.adapter = recipeAdapter
    }


    // Menangani tombol back pada toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish() // Kembali ke halaman sebelumnya
            return true
        }
        return super.onOptionsItemSelected(item)
    }

//    private fun loadFavoriteRecipes() {
//        lifecycleScope.launch {
//            database.recipeDao().getFavoriteRecipes().collect { recipes ->
//                recipeAdapter.submitList(recipes)
//            }
//        }
//    }
}

