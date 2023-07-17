package com.hdd.instaFashion.data.remoteDataSource.response

import com.hdd.instaFashion.data.models.User


data class UserResponse(
    val success: Boolean? = null,
    val data: User? = null,
    val accessToken: String? = null,
    val message: String? = null
)