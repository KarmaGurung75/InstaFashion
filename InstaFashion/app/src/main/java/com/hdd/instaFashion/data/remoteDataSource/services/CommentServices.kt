package com.hdd.instaFashion.data.remoteDataSource.services

import com.hdd.instaFashion.data.models.Comment
import com.hdd.instaFashion.data.remoteDataSource.response.CommentResponse
import com.hdd.instaFashion.data.remoteDataSource.response.CommentsResponse
import retrofit2.Response
import retrofit2.http.*

interface CommentService {
    @POST("comment/{id}")
    suspend fun addComment(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body comment: Comment
    ): Response<CommentsResponse>

    @GET("comment/{id}")
    suspend fun getAllComment(@Path("id") id: String): Response<CommentsResponse>

    @PATCH("comment/{commentId}")
    suspend fun editComment(
        @Header("Authorization") token: String,
        @Path("commentId") commentId: String,
        @Body comment: Comment
    ): Response<CommentResponse>

    @DELETE("comment/{id}")
    suspend fun deleteComment(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<CommentResponse>
}