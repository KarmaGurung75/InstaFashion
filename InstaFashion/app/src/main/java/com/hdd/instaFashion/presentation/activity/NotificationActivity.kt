package com.hdd.instaFashion.presentation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hdd.instaFashion.R
import com.hdd.instaFashion.data.models.Notification
import com.hdd.instaFashion.domain.adapter.NotificationAdapter
import com.hdd.instaFashion.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationActivity : AppCompatActivity() {
    private lateinit var notificationList: MutableList<Notification>
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var notificationRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        notificationRecyclerView = findViewById(R.id.notificationRecyclerView)
        getNotification()
    }
    private fun getNotification() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val notificationRepository= NotificationRepository()
                val response=notificationRepository.getNotification()
                if(response.success==true){
                    notificationList = response.data!!
                    withContext(Dispatchers.Main){
                        notificationAdapter = NotificationAdapter(notificationList.asReversed())
                        val linearLayoutManager= object : LinearLayoutManager(this@NotificationActivity) {
                            override fun canScrollVertically(): Boolean {
                                return true
                            }
                        }
                        notificationRecyclerView.layoutManager= linearLayoutManager
                        notificationRecyclerView.adapter=notificationAdapter
                    }
                }
            }catch (ex:Exception){
                print(ex)
            }
        }
    }
}