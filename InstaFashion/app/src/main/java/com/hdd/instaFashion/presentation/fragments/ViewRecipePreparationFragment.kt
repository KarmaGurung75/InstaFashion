package com.hdd.instaFashion.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.PreparationSchema

class ViewRecipePreparationFragment(private val preparationSchema: PreparationSchema) : Fragment() {

    private lateinit var input_preparation_time: TextView
    private lateinit var input_cooking_time: TextView
    private lateinit var input_total_time: TextView
    private lateinit var input_serving_people: TextView
    private lateinit var input_quantity_pound: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_product_preparation, container, false)
        input_preparation_time = view.findViewById(R.id.input_preparation_time)
        input_cooking_time = view.findViewById(R.id.input_cooking_time)
        input_total_time = view.findViewById(R.id.input_total_time)
        input_serving_people = view.findViewById(R.id.input_serving_people)
        input_quantity_pound = view.findViewById(R.id.input_quantity_pound)

        setPreparationDetails(preparationSchema)
        return view
    }

    private fun setPreparationDetails(preparationSchema:PreparationSchema) {
        input_preparation_time.text = "${preparationSchema.preparation}"
        input_cooking_time.text = "${preparationSchema.cooking}"
        input_total_time.text = "${preparationSchema.preparation!! + preparationSchema.cooking!!}"
        input_serving_people.text = "${preparationSchema.serving}"
        input_quantity_pound.text = "${preparationSchema.yield}"
    }
}