package com.hdd.instaFashion.presentation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.Recipe
import com.hdd.instaFashion.domain.adapter.RecentlyViewAdapter
import com.hdd.instaFashion.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RecentlyViewedActivity : AppCompatActivity() {
    private lateinit var arv_recyclerView : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recently_viewed)

        arv_recyclerView = findViewById(R.id.arv_recyclerView)

        fetchData()
    }

    private fun fetchData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userRepository = UserRepository()
                val response = userRepository.getUserProfile()
                if (response.success == true) {
                    val user = response.data!!
                    withContext(Dispatchers.Main) {
                        val adapter = RecentlyViewAdapter(user.recentlyViewed!!.take(10) as MutableList<Recipe>)
                        arv_recyclerView.layoutManager = GridLayoutManager(this@RecentlyViewedActivity, 2)
                        arv_recyclerView.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                }
            } catch (ex: Exception) {
                print(ex)
            }
        }
    }
}