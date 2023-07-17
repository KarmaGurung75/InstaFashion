package com.hdd.instaFashion.data.remoteDataSource.services

import com.hdd.instaFashion.data.models.ReviewRating
import com.hdd.instaFashion.data.remoteDataSource.response.ReviewRatingResponse
import retrofit2.Response
import retrofit2.http.*

interface ReviewRatingService {
    @POST("review/{id}")
    suspend fun addReviewRating(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body review: ReviewRating
    ): Response<ReviewRatingResponse>

    @GET("review/get/{recipeId}")
    suspend fun getAllReviewRating(
        @Path("recipeId") recipeId: String,
    ): Response<ReviewRatingResponse>

    @PATCH("review/{recipeId}")
    suspend fun updateReviewRating(
        @Header("Authorization") token: String,
        @Path("recipeId") recipeId: String,
        @Body review: ReviewRating
    ): Response<ReviewRatingResponse>

    @DELETE("review/{recipeId}/{reviewId}")
    suspend fun deleteReviewRating(
        @Header("Authorization") token: String,
        @Path("recipeId") recipeId: String,
        @Path("reviewId") reviewId: String,
    ): Response<ReviewRatingResponse>

}