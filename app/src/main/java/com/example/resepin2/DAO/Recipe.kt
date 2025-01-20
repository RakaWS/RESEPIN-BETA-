package com.example.resepin2.DAO

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,           // Nama resep
    val description: String,    // Deskripsi resep
    val ingredients: String,    // Bahan-bahan
    val instructions: String,   // Cara membuat
    val imageUrl: String,       // URL gambar (opsional)
    var isFavorite: Boolean = false  // Status favorit
)