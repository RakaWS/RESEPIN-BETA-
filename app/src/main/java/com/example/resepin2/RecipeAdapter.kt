package com.example.resepin2

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeAdapter(
    private val onRecipeClick: (Food) -> Unit
) : ListAdapter<Food, RecipeAdapter.RecipeViewHolder>(FoodDiffCallback()) {

    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.imgRecipe)
        private val nameText: TextView = view.findViewById(R.id.tvRecipeName)
        private val descriptionText: TextView = view.findViewById(R.id.tvRecipeDescription)

        fun bind(food: Food, onRecipeClick: (Food) -> Unit) {
            nameText.text = food.name
            descriptionText.text = food.description

            Glide.with(imageView.context)
                .load(food.imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(imageView)
            itemView.setOnClickListener { onRecipeClick(food) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position), onRecipeClick)
    }
}

class FoodDiffCallback : DiffUtil.ItemCallback<Food>() {
    override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
        return oldItem == newItem
    }
}
