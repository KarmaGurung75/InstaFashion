package com.hdd.instaFashion.data.remoteDataSource.services

import com.hdd.instaFashion.data.models.Recipe
import com.hdd.instaFashion.data.remoteDataSource.response.RecipeResponse
import com.hdd.instaFashion.data.remoteDataSource.response.RecipesResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface RecipeServices {

    @Multipart
    @POST("recipe")
    suspend fun addRecipe(
        @Header("Authorization") token: String,
        @Part("prevRecipeId") prevRecipeId: RequestBody,
        @Part image: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
    ): Response<RecipeResponse>

    @PATCH("recipe/no-image/{id}")
    suspend fun updateRecipeWithoutImage(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body recipe: Recipe,
    ): Response<RecipeResponse>

    @GET("recipe/{id}")
    suspend fun getRecipeById(
        @Header("Authorization") token: String,
        @Path("id",
        ) id: String): Response<RecipeResponse>

    @PATCH("recipe/preparation/{id}")
    suspend fun updateRecipePreparation(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body recipe: Recipe
    ): Response<RecipeResponse>

    @PATCH("recipe/ingredients/{id}")
    suspend fun updateRecipeIngredients(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body recipe: Recipe
    ): Response<RecipeResponse>

    @PATCH("recipe/direction/{id}")
    suspend fun updateRecipeDirection(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body recipe: Recipe
    ): Response<RecipeResponse>

    @PATCH("recipe/hashtag/{id}")
    suspend fun updateRecipeHashtag(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body recipe: Recipe
    ): Response<RecipeResponse>

    @DELETE("recipe/discard/{id}")
    suspend fun discardRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<RecipeResponse>

    @POST("recipe/share/{id}")
    suspend fun shareRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<RecipeResponse>

    @POST("recipe/ok/{id}")
    suspend fun postRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<RecipeResponse>

    @POST("recipe/archive/{id}")
    suspend fun archivedRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<RecipeResponse>

    @DELETE("recipe/deleteArchived/{id}")
    suspend fun deleteArchived(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<RecipeResponse>

    @GET("recipe/archive")
    suspend fun viewArchivedRecipe(
        @Header("Authorization") token: String,
    ): Response<RecipesResponse>

    @FormUrlEncoded
    @POST("reportRecipe/{id}")
    suspend fun reportRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Field("reason") reason:String
    ): Response<RecipeResponse>

    @DELETE("recipe/{id}")
    suspend fun deleteRecipe(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Response<RecipeResponse>
}