package com.hdd.instaFashion.data.remoteDataSource.response

import com.hdd.instaFashion.data.models.Notification


data class NotificationResponse(
    val success: Boolean? = null,
    val data: MutableList<Notification>? = null,
)