package com.hdd.instaFashion.data.remoteDataSource.services

import com.hdd.instaFashion.data.remoteDataSource.response.*
import retrofit2.Response
import retrofit2.http.*

interface NotificationServices {
    @GET("notification")
    suspend fun getNotification(
        @Header("Authorization") token: String,
    ): Response<NotificationResponse>
}
