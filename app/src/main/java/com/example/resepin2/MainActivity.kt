package com.example.resepin2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.resepin2.DAO.Recipe
import com.example.resepin2.DAO.RecipeDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var database: RecipeDatabase
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi database
        database = RecipeDatabase.getDatabase(this)

        setupRecyclerView()
        setupBottomNavigation()
        loadRecipesFromDatabase()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recipeRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        recipeAdapter = RecipeAdapter { food ->
            // Handle item click to navigate to RecipeDetailActivity
            val intent = Intent(this, RecipeDetailActivity::class.java).apply {
                putExtra("RECIPE_ID", food.id)
            }
            startActivity(intent)
        }
        recyclerView.adapter = recipeAdapter
    }

    private fun loadRecipesFromDatabase() {
        lifecycleScope.launch {
            try {
                // Ambil resep dari database
                val recipes = database.recipeDao().getAllRecipes()

                // Konversi Recipe menjadi Food
                val foodList = recipes.map { recipe ->
                    Food(
                        id = recipe.id,
                        name = recipe.name,
                        description = recipe.description,
                        imageUrl = recipe.imageUrl ?: ""
                    )
                }

                // Update adapter dengan data dari database
                recipeAdapter.submitList(foodList)
            } catch (e: Exception) {
                // Jika gagal mengambil dari database, gunakan data dummy
                val foodList = mutableListOf(
                    Food(1, "Nasi Goreng", "Nasi goreng khas Indonesia.", "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c2/Emmentaler.jpg/356px-Emmentaler.jpg"),
                    Food(2, "Mie Ayam", "Mie ayam dengan topping ayam dan pangsit.", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/67/Orange_juice_1_edit1.jpg/211px-Orange_juice_1_edit1.jpg"),
                    Food(3, "Sate Ayam", "Sate ayam dengan bumbu kacang.", "https://upload.wikimedia.org/wikipedia/commons/thumb/8/84/Keripik_singkong_balado_cassava_chips.JPG/400px-Keripik_singkong_balado_cassava_chips.JPG"),
                    Food(4, "Bakso", "Bakso dengan kuah hangat.", "https://upload.wikimedia.org/wikipedia/commons/thumb/5/57/Port_wine.jpg/400px-Port_wine.jpg")
                )
                recipeAdapter.submitList(foodList)
            }
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottomNavigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Sudah di halaman home, tidak perlu berbuat apa-apa
                    true
                }
                R.id.navigation_add -> {
                    // Pindah ke AddRecipeActivity
                    val intent = Intent(this, AddRecipeActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Muat ulang resep saat kembali ke halaman
        loadRecipesFromDatabase()
    }
}
