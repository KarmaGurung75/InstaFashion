package com.hdd.instaFashion.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.IngredientSchema
import com.hdd.instaFashion.domain.adapter.RecipeIngredientsAdapter


class ViewRecipeIngredientsFragment(private val ingredientList: MutableList<IngredientSchema>) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_product_ingredients, container, false)

        val fvri_recyclerView: RecyclerView = view.findViewById(R.id.fvri_recyclerView)

        val adapter = RecipeIngredientsAdapter(ingredientList,true)
        fvri_recyclerView.layoutManager = LinearLayoutManager(requireContext())
        fvri_recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        return view
    }
}