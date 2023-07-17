package com.hdd.instaFashion.repository

import com.hdd.instaFashion.data.models.ReviewRating
import com.hdd.instaFashion.data.remoteDataSource.HttpRequestNetworkCall
import com.hdd.instaFashion.data.remoteDataSource.ServiceBuilder
import com.hdd.instaFashion.data.remoteDataSource.response.ReviewRatingResponse
import com.hdd.instaFashion.data.remoteDataSource.services.ReviewRatingService


class ReviewRatingRepository : HttpRequestNetworkCall() {
    private val reviewService = ServiceBuilder.buildService(ReviewRatingService::class.java)

    suspend fun getAllReviewRating(recipeId:String): ReviewRatingResponse {
        return myHttpRequestNetworkCall {
            reviewService.getAllReviewRating(recipeId)
        }
    }

    suspend fun addReviewRating(reviewId:String,review:ReviewRating): ReviewRatingResponse {
        return myHttpRequestNetworkCall {
            reviewService.addReviewRating(ServiceBuilder.token!!,reviewId,review)
        }
    }

    suspend fun updateReviewRating(recipeId:String,review:ReviewRating): ReviewRatingResponse {
        return myHttpRequestNetworkCall {
            reviewService.updateReviewRating(ServiceBuilder.token!!,recipeId,review)
        }
    }

    suspend fun deleteReviewRating(recipeId:String,reviewId:String): ReviewRatingResponse {
        return myHttpRequestNetworkCall {
            reviewService.deleteReviewRating(ServiceBuilder.token!!,recipeId,reviewId)
        }
    }
}