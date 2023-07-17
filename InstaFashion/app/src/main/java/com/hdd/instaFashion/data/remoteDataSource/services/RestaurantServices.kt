package com.hdd.instaFashion.data.remoteDataSource.services

import com.hdd.instaFashion.data.models.Restaurant
import com.hdd.instaFashion.data.remoteDataSource.response.ImageResponse
import com.hdd.instaFashion.data.remoteDataSource.response.RestaurantResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface RestaurantServices {

    @Multipart
    @POST("restaurant")
    suspend fun addRestaurantDetail(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("address") address: RequestBody,
        @Part("openingTime") openingTime: RequestBody,
        @Part("closingTime") closingTime: RequestBody,
        @Part("phone") phone: RequestBody,
    ): Response<RestaurantResponse>

    @GET("restaurant/{id}")
    suspend fun getRestaurantById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<RestaurantResponse>

    @PATCH("restaurant/no-image/{id}")
    suspend fun updateRestaurantDetails(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body restaurant: Restaurant
    ): Response<RestaurantResponse>

    @Multipart
    @PATCH("restaurant/restaurant-image/{id}")
    suspend fun updateRestaurantImage(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Part image: MultipartBody.Part
    ): Response<ImageResponse>

    @Multipart
    @PATCH("restaurant/cover-image/{id}")
    suspend fun updateRestaurantCoverImage(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Part image: MultipartBody.Part
    ): Response<ImageResponse>

    @PATCH("restaurant/no-image/{id}")
    suspend fun updateRestaurantDetailsWithoutImage(
        @Header("Authorization") token: String,
        @Path("id") id:String,
        @Body restaurant: Restaurant,
    ): Response<RestaurantResponse>

    @DELETE("restaurant/{id}")
    suspend fun deleteRestaurantDetails(
        @Header("Authorization") token: String,
        @Path("id") id:String,
    ): Response<RestaurantResponse>
}