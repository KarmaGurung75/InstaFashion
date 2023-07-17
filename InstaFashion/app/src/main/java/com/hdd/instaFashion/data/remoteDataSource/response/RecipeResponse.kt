package com.hdd.instaFashion.data.remoteDataSource.response

import com.hdd.instaFashion.data.models.Recipe


data class RecipeResponse(
    val success: Boolean? = null,
    val data: Recipe? = null,
)