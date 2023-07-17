package com.hdd.instaFashion.data.remoteDataSource.response

import com.hdd.instaFashion.data.models.Comment

data class CommentsResponse(
    val success: Boolean? = null,
    val data: MutableList<Comment>? = null,
)