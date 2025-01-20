package com.example.resepin2

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    // Mengambil semua resep
    @Query("SELECT * FROM recipes")
    fun getAllRecipes(): Flow<List<Recipe>>

    // Mengambil resep favorit
    @Query("SELECT * FROM recipes WHERE isFavorite = 1")
    fun getFavoriteRecipes(): Flow<List<Recipe>>

    // Menambah resep baru
    @Insert
    suspend fun insertRecipe(recipe: Recipe)

    // Mengupdate resep
    @Update
    suspend fun updateRecipe(recipe: Recipe)

    // Menghapus resep
    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    // Mengubah status favorit
    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :recipeId")
    suspend fun updateFavoriteStatus(recipeId: Int, isFavorite: Boolean)

    // Mengambil resep berdasarkan ID
    @Query("SELECT * FROM recipes WHERE id = :recipeId LIMIT 1")
    suspend fun getRecipeById(recipeId: Int): Recipe?
}
