package com.hdd.instaFashion.data.remoteDataSource.services

import com.hdd.instaFashion.data.models.User
import com.hdd.instaFashion.data.remoteDataSource.response.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UserServices {
    @POST("user/register")
    suspend fun registerUser(
        @Body user: User,
    ): Response<UserResponse>

    @POST("user/login")
    suspend fun login(@Body user: User): Response<UserResponse>

    @GET("user")
    suspend fun getUserProfile(
        @Header("Authorization") token: String,
    ):Response<UserResponse>

    @GET("user/{id}")
    suspend fun getOtherUserProfile(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ):Response<UserResponse>

    @GET("user/post")
    suspend fun getUserPost(
        @Header("Authorization") token: String,
    ):Response<PostsResponse>

    @GET("user/recipe")
    suspend fun getUserRecipe(
        @Header("Authorization") token: String,
    ):Response<RecipesResponse>

    @PATCH("user")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body user: User
    ):Response<UserResponse>

    @PATCH("user/follow/{id}")
    suspend fun followUser(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ):Response<UserResponse>

    @Multipart
    @PATCH("user/profile")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part profile: MultipartBody.Part
    ): Response<ImageResponse>

    @PATCH("user/password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body user: User,
    ):Response<UserResponse>

    @GET("user/savedRecipe")
    suspend fun getUserSavedRecipe(
        @Header("Authorization") token: String,
    ):Response<RecipesResponse>

    @POST("user/savedRecipe/{recipeId}")
    suspend fun savedRecipe(
        @Header("Authorization") token: String,
        @Path("recipeId") recipeId: String
    ):Response<UserResponse>

    @POST("user/reset")
    suspend fun resetUser(
        @Body user: User,
    ): Response<UserResponse>

    @PATCH("user/new-password")
    suspend fun setNewPassword(
        @Body user: User,
    ): Response<UserResponse>

    @POST("user/validate-email")
    suspend fun validateEmail(
        @Body user: User,
    ): Response<UserResponse>

    @GET("user/followers")
    suspend fun getUserFollowers(
        @Header("Authorization") token: String,
    ):Response<FollowersFollowingResponse>

    @GET("user/following")
    suspend fun getUserFollowing(
        @Header("Authorization") token: String,
    ):Response<FollowersFollowingResponse>
}
