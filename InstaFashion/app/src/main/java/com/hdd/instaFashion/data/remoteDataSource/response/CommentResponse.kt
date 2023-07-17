package com.hdd.instaFashion.data.remoteDataSource.response

import com.hdd.instaFashion.data.models.Comment

data class CommentResponse(
    val success: Boolean? = null,
    val data: Comment? = null,
)