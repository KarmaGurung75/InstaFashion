package com.hdd.instaFashion.data.remoteDataSource.response

import com.hdd.instaFashion.data.models.Post


data class PostResponse(
    val success: Boolean? = null,
    val data: Post? = null,
    val accessToken: String? = null
)