package com.hdd.instaFashion.domain.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.Recipe
import com.hdd.instaFashion.presentation.activity.ProductDetailsActivity

class RecentlyViewAdapter(private val recipeList: MutableList<Recipe>) :
    RecyclerView.Adapter<RecentlyViewedViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentlyViewedViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.product_recycler_view, parent, false)
        return RecentlyViewedViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentlyViewedViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.bind(recipe)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProductDetailsActivity::class.java)
            val bundle = Bundle()
            bundle.putString("recipeId", recipe._id)
            intent.putExtras(bundle)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }
}

class RecentlyViewedViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val rcvRecipeName: TextView = view.findViewById(R.id.rcvRecipeName)
    private val rcvRecipeImage: ImageView = view.findViewById(R.id.rcvRecipeImage)
    fun bind(recipe: Recipe) {
        Glide.with(itemView.context).load(recipe.image).into(rcvRecipeImage)
        rcvRecipeName.text = recipe.title
    }
}