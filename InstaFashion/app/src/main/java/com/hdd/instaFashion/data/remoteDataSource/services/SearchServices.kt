package com.hdd.instaFashion.data.remoteDataSource.services

import com.hdd.instaFashion.data.models.Search
import com.hdd.instaFashion.data.remoteDataSource.response.SearchResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface SearchServices {
    @POST("search")
    suspend fun search(
        @Body search: Search,
    ): Response<SearchResponse>
}