package com.apricot.app.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.apricot.app.R
import com.apricot.app.data.model.AbstractRecipe
import com.apricot.app.data.model.RecipeInComplexSearch

class RecipeAdapter(
    public var recipesList: List<AbstractRecipe>,
    private val onClick: (AbstractRecipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    // The ViewHolder "finds" the views in the card
    inner class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgRecipe: ImageView = view.findViewById(R.id.imgRecipe)
        val textViewRecipeTitle: TextView = view.findViewById(R.id.textViewRecipeTitle)
        val textViewRecipeType: TextView = view.findViewById(R.id.textViewRecipeType)

        fun bind(recipe: AbstractRecipe) {
            textViewRecipeTitle.text = recipe.title
            imgRecipe.load(recipe.imageUrl) {crossfade(true)}
            itemView.setOnClickListener { onClick(recipe) }
        }
    }

    // Create the view from recipe_item.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(view)
    }

    // Bind recipe data to the created view
    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(recipesList[position])
    }

    override fun getItemCount(): Int {
        return recipesList.size
    }
}