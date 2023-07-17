package com.hdd.instaFashion.repository

import com.hdd.instaFashion.data.remoteDataSource.HttpRequestNetworkCall
import com.hdd.instaFashion.data.remoteDataSource.ServiceBuilder
import com.hdd.instaFashion.data.remoteDataSource.response.*
import com.hdd.instaFashion.data.remoteDataSource.services.NotificationServices


class NotificationRepository : HttpRequestNetworkCall() {
    private val notificationService = ServiceBuilder.buildService(NotificationServices::class.java)

    suspend fun getNotification(): NotificationResponse {
        return myHttpRequestNetworkCall {
            notificationService.getNotification(ServiceBuilder.token!!)
        }
    }
}