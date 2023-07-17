package com.hdd.instaFashion.domain.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.IngredientSchema

class RecipeIngredientsAdapter(private val recipeIngredientsList: MutableList<IngredientSchema>,private val isViewing:Boolean=false): RecyclerView.Adapter<RecipeIngredientsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeIngredientsViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.product_directions_items,parent,false)
        return RecipeIngredientsViewHolder(view)
    }
    override fun onBindViewHolder(holder: RecipeIngredientsViewHolder, position: Int) {
        val ingredients = recipeIngredientsList[position]
        holder.rdi_stepName.text= "${ingredients.quantity} ${ingredients.unit} ${ingredients.item}"
        if (isViewing){
            holder.rdi_stepCancel.visibility=View.GONE
        }
        holder.rdi_stepCancel.setOnClickListener{
            recipeIngredientsList.remove(ingredients)
            Toast.makeText(it.context, "Item removed", Toast.LENGTH_SHORT).show()
            notifyDataSetChanged()
        }

    }
    override fun getItemCount(): Int {
        return recipeIngredientsList.size
    }
}
class RecipeIngredientsViewHolder(view: View) : RecyclerView.ViewHolder(view){
    val rdi_stepName: TextView =view.findViewById(R.id.rdi_stepName)
    val rdi_stepCancel: ImageButton =view.findViewById(R.id.rdi_stepCancel)
}