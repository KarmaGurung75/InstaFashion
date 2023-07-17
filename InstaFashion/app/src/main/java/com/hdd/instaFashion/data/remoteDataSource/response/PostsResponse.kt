package com.hdd.instaFashion.data.remoteDataSource.response

import com.hdd.instaFashion.data.models.Post


data class PostsResponse(
    val success: Boolean? = null,
    val data: MutableList<Post>? = null,
)