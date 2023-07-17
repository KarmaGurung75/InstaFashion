package com.hdd.instaFashion.data.remoteDataSource.response

import com.hdd.instaFashion.data.models.Recipe


data class RecipesResponse(
    val success: Boolean? = null,
    val data: MutableList<Recipe>? = null,
)