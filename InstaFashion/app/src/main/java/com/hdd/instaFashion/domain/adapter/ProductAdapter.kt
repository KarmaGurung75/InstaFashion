package com.hdd.instaFashion.domain.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.Recipe
import com.hdd.instaFashion.presentation.activity.ProductDetailsActivity
import com.hdd.instaFashion.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipeAdapter(
    private val recipeList: MutableList<Recipe>,
    private val isArchived: Boolean = false
) :
    RecyclerView.Adapter<ProfileRecipeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileRecipeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.product_recycler_view, parent, false)
        return ProfileRecipeViewHolder(view)
    }
    override fun onBindViewHolder(holder: ProfileRecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.bind(recipe, isArchived, recipeList)

        holder.itemView.setOnLongClickListener {
            val archiveDialog = Dialog(holder.itemView.context)
            archiveDialog.setContentView(R.layout.archive_product_layout)
            archiveDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            val archive: Button = archiveDialog.findViewById(R.id.arl_archive_recipe)
            val delete: Button = archiveDialog.findViewById(R.id.arl_delete_recipe)
            // archive
            if (isArchived){
                archive.text="Unarchive"
                archive.setOnClickListener {
                    archivedRecipe(recipe._id)
                    recipeList.remove(recipe)
                    notifyDataSetChanged()
                    Toast.makeText(holder.itemView.context, "Recipe Unarchived", Toast.LENGTH_SHORT).show()
                    archiveDialog.dismiss()
                }
            } else {
                archive.text = "Archive"
                archive.setOnClickListener {
                    archivedRecipe(recipe._id)
                    recipeList.remove(recipe)
                    notifyDataSetChanged()
                    Toast.makeText(holder.itemView.context, "Recipe archived", Toast.LENGTH_SHORT).show()
                    archiveDialog.dismiss()
                }
            }
            // delete review and rating
            delete.setOnClickListener {
                showDeleteAlertDialog(holder.itemView.context,recipe._id,recipe)
                archiveDialog.dismiss()
            }
            archiveDialog.show()
            true
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ProductDetailsActivity::class.java)
            val bundle = Bundle()
            bundle.putString("recipeId", recipe._id)
            intent.putExtras(bundle)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }
        private fun showDeleteAlertDialog(context: Context,recipeId: String,recipe: Recipe) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete this?")
        builder.setMessage("Are you sure want to delete?")
        builder.setIcon(R.mipmap.sign_out)
        //performing Positive action
        builder.setPositiveButton("Yes") { _, _ ->
            deleteRecipe(recipeId)
            recipeList.remove(recipe)
            notifyDataSetChanged()
            Toast.makeText(context,"Recipe Deleted",Toast.LENGTH_SHORT).show()
        }
        //performing cancel action
        builder.setNeutralButton("Cancel") { _, _ ->
        }

        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

    }
    private fun archivedRecipe(recipeId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val productRepository = ProductRepository()
                productRepository.archivedRecipe(recipeId)
            } catch (ex: Exception) {
                print(ex)
            }
        }
    }
    private fun deleteRecipe(recipeId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val productRepository= ProductRepository()
                productRepository.deleteRecipe(recipeId)
            } catch (ex: java.lang.Exception){
                print(ex)
            }
        }
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }
}
class ProfileRecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val rcvRecipeName: TextView = view.findViewById(R.id.rcvRecipeName)
    private val rcvRecipeImage: ImageView = view.findViewById(R.id.rcvRecipeImage)
    //    private val archiveSetting: ImageButton = view.findViewById(R.id.archiveSetting)
    fun bind(recipe: Recipe, isArchived: Boolean, recipeList: MutableList<Recipe>) {
        Glide.with(itemView.context).load(recipe.image).into(rcvRecipeImage)
        rcvRecipeName.text = recipe.title
//        archiveSetting.setOnClickListener {
//            loadPopUpSetting(recipe._id) }
    }
}