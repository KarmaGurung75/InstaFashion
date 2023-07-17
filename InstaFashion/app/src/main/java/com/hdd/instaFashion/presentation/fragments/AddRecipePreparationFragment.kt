package com.hdd.instaFashion.presentation.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.PreparationSchema
import com.hdd.instaFashion.data.models.Recipe
import com.hdd.instaFashion.data.remoteDataSource.ServiceBuilder
import com.hdd.instaFashion.repository.ProductRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddRecipePreparationFragment(private val isEditMode:Boolean=false,private val preparationSchema: PreparationSchema= PreparationSchema()) : Fragment() {
    private lateinit var input_preparation_time:TextView
    private lateinit var input_cooking_time:TextView
    private lateinit var input_total_time:TextView
    private lateinit var input_serving_people:TextView
    private lateinit var input_quantity_pound:TextView
    private lateinit var recipe_empty_preparation_details:TextView
    private lateinit var btnAddPreparationDetails:Button
    private lateinit var preparationLayout:LinearLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var isNotFilled = true
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_product_preparation, container, false)
        input_preparation_time= view.findViewById(R.id.input_preparation_time)
        input_cooking_time= view.findViewById(R.id.input_cooking_time)
        input_total_time= view.findViewById(R.id.input_total_time)
        input_serving_people= view.findViewById(R.id.input_serving_people)
        input_quantity_pound= view.findViewById(R.id.input_quantity_pound)
        recipe_empty_preparation_details= view.findViewById(R.id.recipe_empty_preparation_details)
        btnAddPreparationDetails= view.findViewById(R.id.btnAddPreparationDetails)
        preparationLayout= view.findViewById(R.id.preparationLayout)
        preparationLayout.visibility = View.GONE

        // in case of update or edit
        if (isEditMode){
            input_preparation_time.text="${preparationSchema.preparation}"
            input_cooking_time.text="${preparationSchema.cooking}"
            input_total_time.text="${preparationSchema.preparation!!+preparationSchema.cooking!!}"
            input_serving_people.text= "${preparationSchema.serving}"
            input_quantity_pound.text="${preparationSchema.yield}"
            isNotFilled=false
            preparationLayout.visibility = View.VISIBLE
            recipe_empty_preparation_details.visibility = View.GONE
            btnAddPreparationDetails.text = "Edit Details"
        }

        btnAddPreparationDetails.setOnClickListener {
            if (isNotFilled) {
                val reviewDialog = Dialog(requireContext())
                reviewDialog.setContentView(R.layout.add_preparation_dialog)
                reviewDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                val etUpdatePreparationTime: EditText = reviewDialog.findViewById(R.id.etAddPreparationTime)
                val etUpdateCookingTime: EditText = reviewDialog.findViewById(R.id.etAddCookingTime)
                val etUpdateServingPerson: EditText = reviewDialog.findViewById(R.id.etAddServingPerson)
                val etUpdateQtyPound: EditText = reviewDialog.findViewById(R.id.etAddQtyPound)
                val cancel: Button = reviewDialog.findViewById(R.id.apd_cancel)
                val ok: Button = reviewDialog.findViewById(R.id.apd_ok)
                cancel.setOnClickListener {
                    reviewDialog.dismiss()
                    recipe_empty_preparation_details.visibility = View.VISIBLE
                    btnAddPreparationDetails.text = "Add Details"
                }
                ok.setOnClickListener {
                    if (
                        etUpdateCookingTime.text.isNotEmpty() && etUpdatePreparationTime.text.isNotEmpty() && etUpdateServingPerson.text.isNotEmpty() && etUpdateQtyPound.text.isNotEmpty()
                    ){
                        input_preparation_time.text = "${etUpdatePreparationTime.text}"
                        input_cooking_time.text = "${etUpdateCookingTime.text}"
                        input_total_time.text = "${etUpdatePreparationTime.text.toString().toInt()+etUpdateCookingTime.text.toString().toInt()}"
                        input_serving_people.text = "${etUpdateServingPerson.text}"
                        input_quantity_pound.text = "${etUpdateQtyPound.text}"
                        isNotFilled = false
                        preparationLayout.visibility = View.VISIBLE
                        recipe_empty_preparation_details.visibility = View.GONE
                        updateRecipePreparation(
                            ServiceBuilder.recipeId,
                            etUpdatePreparationTime.text.toString(),
                            etUpdateCookingTime.text.toString(),
                            etUpdateServingPerson.text.toString(),
                            etUpdateQtyPound.text.toString(),
                        )
                        btnAddPreparationDetails.text = "Edit Details"
                        reviewDialog.dismiss()
                    } else{
                        etUpdatePreparationTime.error="This is required"
                        etUpdateCookingTime.error="This is required"
                        etUpdateServingPerson.error="This is required"
                        etUpdateQtyPound.error="This is required"
                    }
                }
                reviewDialog.show()
            }
            else {
                recipe_empty_preparation_details.visibility = View.GONE
                btnAddPreparationDetails.setOnClickListener {
                    val reviewDialog = Dialog(requireContext())
                    reviewDialog.setContentView(R.layout.update_preparation_dialog)
                    reviewDialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                    val etUpdatePreparationTime: EditText = reviewDialog.findViewById(R.id.etUpdatePreparationTime)
                    val etUpdateCookingTime: EditText = reviewDialog.findViewById(R.id.etUpdateCookingTime)
                    val etUpdateServingPerson: EditText = reviewDialog.findViewById(R.id.etUpdateServingPerson)
                    val etUpdateQtyPound: EditText = reviewDialog.findViewById(R.id.etUpdateQtyPound)
                    val cancel: Button = reviewDialog.findViewById(R.id.upd_cancel)
                    val btnUpdate: Button = reviewDialog.findViewById(R.id.upd_update)
                    etUpdatePreparationTime.setText(input_preparation_time.text)
                    etUpdateCookingTime.setText(input_cooking_time.text)
                    etUpdateServingPerson.setText(input_serving_people.text)
                    etUpdateQtyPound.setText(input_quantity_pound.text)
                    cancel.setOnClickListener {
                        reviewDialog.dismiss()
                    }
                    btnUpdate.setOnClickListener {
                        if (etUpdateCookingTime.text.isNotEmpty() && etUpdatePreparationTime.text.isNotEmpty() && etUpdateServingPerson.text.isNotEmpty() && etUpdateQtyPound.text.isNotEmpty()){
                            preparationLayout.visibility = View.VISIBLE
                            input_preparation_time.text = "${etUpdatePreparationTime.text}"
                            input_cooking_time.text = "${etUpdateCookingTime.text}"
                            input_total_time.text = "${etUpdatePreparationTime.text.toString().toInt()+etUpdateCookingTime.text.toString().toInt()}"
                            input_serving_people.text = "${etUpdateServingPerson.text}"
                            input_quantity_pound.text = "${etUpdateQtyPound.text}"
                            isNotFilled = false
                            updateRecipePreparation(
                                ServiceBuilder.recipeId,
                                etUpdatePreparationTime.text.toString(),
                                etUpdateCookingTime.text.toString(),
                                etUpdateServingPerson.text.toString(),
                                etUpdateQtyPound.text.toString(),
                            )
                            preparationLayout.visibility = View.VISIBLE
                            recipe_empty_preparation_details.visibility = View.GONE
                            btnAddPreparationDetails.text = "Edit Details"
                            reviewDialog.dismiss()
                        } else {
                            etUpdatePreparationTime.error="This is required"
                            etUpdateCookingTime.error="This is required"
                            etUpdateServingPerson.error="This is required"
                            etUpdateQtyPound.error="This is required"
                        }

                    }
                    reviewDialog.show()
                }
            }
        }
        return view
    }

    private fun updateRecipePreparation(recipeId: String?, preparation: String, cooking: String, serving: String, yieldQty: String) {
        val preparationSchema = PreparationSchema(preparation, cooking, serving, yieldQty)
        val recipe = Recipe(preparation = preparationSchema)
        try {
            val productRepository = ProductRepository()
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    productRepository.updateRecipePreparation(recipeId!!,recipe)
                }
            }
        } catch (ex: Exception) {
            println(ex)
        }
    }

}