package com.hdd.instaFashion.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.User
import com.hdd.instaFashion.domain.adapter.RecipeAdapter


class ViewOtherRecipeFragment(private val user: User) : Fragment() {

    private lateinit var frvo_profileRecipeRV:RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_product_view_other, container, false)
        frvo_profileRecipeRV = view.findViewById(R.id.frvo_profileRecipeRV)
        val adapter = RecipeAdapter(user.recipe!!)
        frvo_profileRecipeRV.layoutManager = LinearLayoutManager(requireActivity())
        frvo_profileRecipeRV.adapter = adapter
        return view
    }
}