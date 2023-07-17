package com.hdd.instaFashion.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hdd.instaFashion.R
import com.hdd.instaFashion.domain.adapter.RecipeDirectionAdapter

class ViewRecipeDirectionsFragment(private val directionList: MutableList<String>) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_view_product_directions, container, false)

        val fvrd_recyclerView :RecyclerView = view.findViewById(R.id.fvrd_recyclerView)

        val linearLayoutManager = object : LinearLayoutManager(requireContext()) {
            override fun canScrollVertically(): Boolean {
                return true
            }
        }

        val adapter = RecipeDirectionAdapter(directionList,true)
        fvrd_recyclerView.layoutManager = linearLayoutManager
        fvrd_recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()

        return view
    }

}