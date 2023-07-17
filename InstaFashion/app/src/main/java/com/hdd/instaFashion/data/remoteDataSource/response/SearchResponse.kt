package com.hdd.instaFashion.data.remoteDataSource.response

import com.hdd.instaFashion.data.models.Search


data class SearchResponse(
    val success: Boolean? = null,
    val data: Search? = null,
    val message: String? = null
)