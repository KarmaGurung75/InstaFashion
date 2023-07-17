package com.hdd.instaFashion.presentation.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.IngredientSchema
import com.hdd.instaFashion.data.models.Recipe
import com.hdd.instaFashion.data.remoteDataSource.ServiceBuilder
import com.hdd.instaFashion.domain.adapter.RecipeIngredientsAdapter
import com.hdd.instaFashion.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class AddRecipeIngredientsFragment(private val isEditMode:Boolean=false,private val ingredientList: MutableList<IngredientSchema> =mutableListOf())  : Fragment() {
    private var isDone: Boolean = false
    private lateinit var adapter : RecipeIngredientsAdapter
    private lateinit var recipeIngredientsList : MutableList<IngredientSchema>

    private lateinit var fari_btnAddIngredientsDetails : Button
    private lateinit var fari_recyclerView : RecyclerView
    private lateinit var fari_btnDone : Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_product_ingredients, container, false)

        fari_recyclerView= view.findViewById(R.id.fari_recyclerView)
        fari_btnAddIngredientsDetails= view.findViewById(R.id.fari_btnAddIngredientsDetails)
        fari_btnDone= view.findViewById(R.id.fari_btnDone)

        val utensils = arrayOf(
            "tablespoon", "teaspoon", "cup", "fluid ounce", "pint", "quart", "pound"
        )
        recipeIngredientsList = mutableListOf<IngredientSchema>()

        // for edit recipe case only
        if (isEditMode){
            recipeIngredientsList=ingredientList
            adapter = RecipeIngredientsAdapter(recipeIngredientsList)
            fari_recyclerView.layoutManager = LinearLayoutManager(requireContext())
            fari_recyclerView.adapter=adapter
            adapter.notifyDataSetChanged()
            fari_btnAddIngredientsDetails.text="Add More Details"
            fari_btnDone.visibility=View.INVISIBLE
            isDone=true
        }

        adapter = RecipeIngredientsAdapter(recipeIngredientsList)
        fari_btnAddIngredientsDetails.setOnClickListener {
            openDialog(utensils)

            fari_recyclerView.layoutManager = LinearLayoutManager(requireContext())
            fari_recyclerView.adapter = adapter
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
            itemTouchHelper.attachToRecyclerView(fari_recyclerView)
            fari_btnDone.setOnClickListener {
                updateRecipeIngredients(ServiceBuilder.recipeId, recipeIngredientsList)
                isDone = true
                fari_btnDone.visibility = View.GONE
            }
        }

        return view
    }

    private fun openDialog(utensils:Array<String>) {
        val ingredientsDialog = Dialog(requireContext())
        ingredientsDialog.setContentView(R.layout.add_ingredients_dialog)
        ingredientsDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        val ingredientUtensils:NumberPicker = ingredientsDialog.findViewById(R.id.ingredientUtensils)
        val servingPeople: EditText = ingredientsDialog.findViewById(R.id.servingPeople)
        val ingredientsItem: EditText = ingredientsDialog.findViewById(R.id.ingredientsItem)
        val cancel: Button = ingredientsDialog.findViewById(R.id.aid_cancel)
        val btnOk: Button = ingredientsDialog.findViewById(R.id.aid_ok)
        ingredientUtensils.minValue = 0
        ingredientUtensils.maxValue = utensils.size - 1
        ingredientUtensils.displayedValues = utensils
        ingredientUtensils.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        cancel.setOnClickListener {
            ingredientsDialog.dismiss()
        }
        btnOk.setOnClickListener {
            if (servingPeople.text.isNotEmpty() && ingredientsItem.text.isNotEmpty()){
                fari_btnDone.visibility=View.VISIBLE
                generateList(servingPeople.text.toString(),utensils[ingredientUtensils.value],ingredientsItem.text.toString())
                ingredientsDialog.dismiss()
            } else {
                servingPeople.error = "This is required"
                ingredientsItem.error = "This is required"
            }
        }
        ingredientsDialog.show()
    }

    private fun generateList(quantity: String, ofUtensils: String, ofIngredientsItem: String) {
        recipeIngredientsList.add(IngredientSchema(quantity.toInt(),ofUtensils,ofIngredientsItem))
        adapter.notifyDataSetChanged()
    }

    private val itemTouchHelperCallback = object :
        ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.START or ItemTouchHelper.END or ItemTouchHelper.UP or ItemTouchHelper.DOWN,0) {
        override fun onMove(recyclerView: RecyclerView,viewHolder: RecyclerView.ViewHolder,target: RecyclerView.ViewHolder): Boolean {
            when {
                isDone -> {
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition =target.adapterPosition
                    Collections.swap(recipeIngredientsList,fromPosition,toPosition)
                    fari_btnDone.visibility=View.VISIBLE
                    recyclerView.adapter!!.notifyItemMoved(fromPosition,toPosition)
                    return false
                }
                else -> {
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition =target.adapterPosition
                    Collections.swap(recipeIngredientsList,fromPosition,toPosition)
                    recyclerView.adapter!!.notifyItemMoved(fromPosition,toPosition)
                    return false
                }
            }
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        }

    }

    private fun updateRecipeIngredients(recipeId: String?, recipeIngredientsList: MutableList<IngredientSchema>) {
        val recipe = Recipe(ingredients = recipeIngredientsList)
        try {
            val productRepository = ProductRepository()
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    productRepository.updateRecipeIngredients(recipeId!!,recipe)
                }
            }
        } catch (ex: Exception) {
            println(ex)
        }
    }
}