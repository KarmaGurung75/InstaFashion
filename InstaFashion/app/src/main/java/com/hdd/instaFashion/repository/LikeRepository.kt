package com.hdd.instaFashion.repository

import com.hdd.instaFashion.data.remoteDataSource.HttpRequestNetworkCall
import com.hdd.instaFashion.data.remoteDataSource.ServiceBuilder
import com.hdd.instaFashion.data.remoteDataSource.response.LikeResponse
import com.hdd.instaFashion.data.remoteDataSource.services.LikeService


class LikeRepository : HttpRequestNetworkCall() {
    private val likeService = ServiceBuilder.buildService(LikeService::class.java)

    suspend fun updateLike(postId:String): LikeResponse {
        return myHttpRequestNetworkCall {
            likeService.updateLike(ServiceBuilder.token!!,postId)
        }
    }
}

