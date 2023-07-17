package com.hdd.instaFashion.data.remoteDataSource.services

import com.hdd.instaFashion.data.remoteDataSource.response.LikeResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path
interface LikeService {

    @PATCH("post/like/{id}")
    suspend fun updateLike(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<LikeResponse>

}

