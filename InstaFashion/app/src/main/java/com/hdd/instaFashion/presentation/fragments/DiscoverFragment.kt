package com.hdd.instaFashion.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.Search
import com.hdd.instaFashion.domain.adapter.RecipeAdapter
import com.hdd.instaFashion.domain.adapter.SearchUserAdapter
import com.hdd.instaFashion.repository.SearchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class DiscoverFragment() : Fragment() {
    private lateinit var svSearch: SearchView
    private lateinit var searchRecipeRecyclerView: RecyclerView
    private lateinit var searchUserRecyclerView: RecyclerView
    private lateinit var showOnEmptySearch: ImageView
    private lateinit var tvSearchedRecipe: TextView
    private lateinit var tvSearchedUser: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        svSearch = view.findViewById(R.id.svSearch)
        searchRecipeRecyclerView = view.findViewById(R.id.searchRecipeRecyclerView)
        searchUserRecyclerView = view.findViewById(R.id.searchUserRecyclerView)
        showOnEmptySearch = view.findViewById(R.id.showOnEmptySearch)
        tvSearchedRecipe = view.findViewById(R.id.tvSearchedRecipe)
        tvSearchedUser = view.findViewById(R.id.tvSearchedUser)
        svSearch.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }
                override fun onQueryTextChange(query: String?): Boolean {
                    if (query!!.trim().isNotEmpty()) {
                        search(Search(pattern = query))
                        return true
                    }
                    return false
                }
            }
        )
        return view
    }

    private fun search(pattern: Search) {
        try {
            val searchRepository = SearchRepository()
            CoroutineScope(Dispatchers.IO).launch {
                val response = searchRepository.search(pattern)
                withContext(Dispatchers.Main) {
                    val user = response.data!!.user!!
                    val recipe = response.data!!.recipe!!
                    if (user.size == 0 && recipe.size == 0) {
                        showOnEmptySearch.visibility = View.VISIBLE
                    } else {
                        tvSearchedRecipe.visibility = View.VISIBLE
                        tvSearchedUser.visibility = View.VISIBLE
                        if (user.size == 0) {
                            tvSearchedUser.visibility = View.GONE
                        }
                        if (recipe.size == 0) {
                            tvSearchedRecipe.visibility = View.GONE
                        }
                        showOnEmptySearch.visibility = View.GONE

                        val searchUserAdapter = SearchUserAdapter(user)
                        val gridRecipeAdapter = RecipeAdapter(recipe)
                        searchUserRecyclerView.layoutManager = LinearLayoutManager(
                            requireContext(),
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        searchRecipeRecyclerView.layoutManager =
                            GridLayoutManager(requireContext(), 2)
                        searchUserRecyclerView.adapter = searchUserAdapter
                        searchRecipeRecyclerView.adapter = gridRecipeAdapter
                    }
                }
            }
        } catch (ex: Exception) {
            println(ex)
        }
    }
}