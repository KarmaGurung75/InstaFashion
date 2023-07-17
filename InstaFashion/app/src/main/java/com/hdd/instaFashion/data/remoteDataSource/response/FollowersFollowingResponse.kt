package com.hdd.instaFashion.data.remoteDataSource.response

import com.hdd.instaFashion.data.models.User


data class FollowersFollowingResponse(
    val success: Boolean? = null,
    val data: MutableList<User>? = null,
    val accessToken: String? = null,
    val message: String? = null
)