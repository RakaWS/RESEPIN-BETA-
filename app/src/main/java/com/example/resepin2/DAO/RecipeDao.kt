package com.example.resepin2.DAO

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    // Mengambil semua resep sebagai Flow
    @Query("SELECT * FROM recipes")
    suspend fun getAllRecipes(): List<Recipe>

    // Mengambil resep favorit sebagai Flow
    @Query("SELECT * FROM recipes WHERE isFavorite = 1")
    suspend fun getFavoriteRecipes(): List<Recipe>

    // Mengambil semua resep sebagai Flow untuk observasi real-time
    @Query("SELECT * FROM recipes")
    fun getAllRecipesFlow(): Flow<List<Recipe>>

    // Mengambil resep favorit sebagai Flow untuk observasi real-time
    @Query("SELECT * FROM recipes WHERE isFavorite = 1")
    fun getFavoriteRecipesFlow(): Flow<List<Recipe>>

    // Menambah resep baru
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe)

    // Menambah beberapa resep sekaligus
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<Recipe>)

    // Mengupdate resep
    @Update
    suspend fun updateRecipe(recipe: Recipe)

    // Menghapus resep
    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    // Mengubah status favorit
    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :recipeId")
    suspend fun updateFavoriteStatus(recipeId: Int, isFavorite: Boolean)


    // Mencari resep berdasarkan nama
    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%'")
    suspend fun searchRecipesByName(query: String): List<Recipe>

    // Menghitung jumlah total resep
    @Query("SELECT COUNT(*) FROM recipes")
    suspend fun getRecipeCount(): Int

    // Menghitung jumlah resep favorit
    @Query("SELECT COUNT(*) FROM recipes WHERE isFavorite = 1")
    suspend fun getFavoriteRecipeCount(): Int

    // Mengambil resep berdasarkan ID
    @Query("SELECT * FROM recipes WHERE id = :recipeId LIMIT 1")
    suspend fun getRecipeById(recipeId: Int): Recipe?

    // Menghapus resep berdasarkan ID
    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteRecipeById(recipeId: Int)


}