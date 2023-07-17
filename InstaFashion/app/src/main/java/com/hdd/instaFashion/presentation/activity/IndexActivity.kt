package com.hdd.instaFashion.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hdd.instaFashion.R
import com.hdd.instaFashion.domain.adapter.PostAdapter
import com.hdd.instaFashion.repository.PostRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IndexActivity : AppCompatActivity() {
    private lateinit var postRecyclerView: RecyclerView
    private lateinit var btnProfile: Button
    private lateinit var swipeDownToRefresh: SwipeRefreshLayout

    override fun onResume() {
        super.onResume()
        getPosts()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)
        postRecyclerView = findViewById(R.id.postRecyclerView)
        btnProfile = findViewById(R.id.btnProfile)
        swipeDownToRefresh = findViewById(R.id.swipeDownToRefresh)
        getPosts()
        swipeDownToRefresh.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            getPosts()
            val handler = Handler()
            handler.postDelayed(Runnable {
                if (swipeDownToRefresh.isRefreshing) {
                    swipeDownToRefresh.isRefreshing = false
                }
            }, 3000)
        })
        btnProfile.setOnClickListener{
            startActivity(Intent(this, ViewOtherProfileActivity::class.java))
        }

    }

    private fun getPosts() {
        CoroutineScope(Dispatchers.IO).launch {
            val postRepository = PostRepository()
            val response = postRepository.getAllPost()
            if(response.success == true){
                withContext(Dispatchers.Main){
                    val adapter = PostAdapter(response.data!!)
                    postRecyclerView.layoutManager = LinearLayoutManager(this@IndexActivity)
                    postRecyclerView.adapter = adapter
                }
            }
        }
    }
}