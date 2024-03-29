package com.hdd.instaFashion.domain.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.hdd.instaFashion.R

class RecipeDirectionAdapter(private val recipeDirectionList: MutableList<String>,private val isViewing:Boolean=false): RecyclerView.Adapter<RecipeDirectionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeDirectionViewHolder {
        val inflater= LayoutInflater.from(parent.context)
        val view=inflater.inflate(R.layout.product_directions_items,parent,false)
        return RecipeDirectionViewHolder(view)
    }
    override fun onBindViewHolder(holder: RecipeDirectionViewHolder, position: Int) {
        val direction = recipeDirectionList[position]
        holder.rdi_stepName.text= direction
        if (isViewing){
            holder.rdi_stepCancel.visibility=View.GONE
        }
        holder.rdi_stepCancel.setOnClickListener{
            recipeDirectionList.remove(direction)
            Toast.makeText(it.context, "Item removed", Toast.LENGTH_SHORT).show()
            notifyDataSetChanged()
        }

    }
    override fun getItemCount(): Int {
        return recipeDirectionList.size
    }
}
class RecipeDirectionViewHolder(view: View) : RecyclerView.ViewHolder(view){
    val rdi_stepNumber: TextView =view.findViewById(R.id.rdi_stepNumber)
    val rdi_stepName: TextView =view.findViewById(R.id.rdi_stepName)
    val rdi_stepCancel: ImageButton =view.findViewById(R.id.rdi_stepCancel)
}