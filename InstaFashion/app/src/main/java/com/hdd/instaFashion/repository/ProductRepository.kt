package com.hdd.instaFashion.repository

import com.hdd.instaFashion.data.models.Recipe
import com.hdd.instaFashion.data.remoteDataSource.HttpRequestNetworkCall
import com.hdd.instaFashion.data.remoteDataSource.ServiceBuilder
import com.hdd.instaFashion.data.remoteDataSource.response.RecipeResponse
import com.hdd.instaFashion.data.remoteDataSource.response.RecipesResponse
import com.hdd.instaFashion.data.remoteDataSource.services.RecipeServices
import okhttp3.MultipartBody
import okhttp3.RequestBody


class ProductRepository : HttpRequestNetworkCall() {
    private val recipeService = ServiceBuilder.buildService(RecipeServices::class.java)

    suspend fun addRecipe(image: MultipartBody.Part,recipeTitle: RequestBody, recipeDescription: RequestBody,prevRecipeId: RequestBody): RecipeResponse {
        return myHttpRequestNetworkCall {
            recipeService.addRecipe(ServiceBuilder.token!!, prevRecipeId,image,recipeTitle, recipeDescription)
        }
    }

    suspend fun updateRecipeWithoutImage(prevRecipeId: String,recipe: Recipe): RecipeResponse {
        return myHttpRequestNetworkCall {
            recipeService.updateRecipeWithoutImage(ServiceBuilder.token!!, prevRecipeId,recipe)
        }
    }

    suspend fun getRecipeById(recipeId: String): RecipeResponse {
        return myHttpRequestNetworkCall {
            recipeService.getRecipeById(ServiceBuilder.token!!,recipeId)
        }
    }
    suspend fun updateRecipePreparation(recipeId: String, recipe: Recipe): RecipeResponse {
        return myHttpRequestNetworkCall {
            recipeService.updateRecipePreparation(ServiceBuilder.token!!, recipeId, recipe  )
        }
    }

    suspend fun updateRecipeDirection(recipeId: String, recipe: Recipe): RecipeResponse {
        return myHttpRequestNetworkCall {
            recipeService.updateRecipeDirection(ServiceBuilder.token!!, recipeId, recipe  )
        }
    }

    suspend fun updateRecipeHashtag(recipeId: String, recipe: Recipe): RecipeResponse {
        return myHttpRequestNetworkCall {
            recipeService.updateRecipeHashtag(ServiceBuilder.token!!, recipeId, recipe  )
        }
    }
    suspend fun updateRecipeIngredients(recipeId: String, recipe: Recipe): RecipeResponse {
        return myHttpRequestNetworkCall {
            recipeService.updateRecipeIngredients(ServiceBuilder.token!!, recipeId, recipe  )
        }
    }
    suspend fun discardRecipe(recipeId: String): RecipeResponse {
        return myHttpRequestNetworkCall {
            recipeService.discardRecipe(ServiceBuilder.token!!, recipeId  )
        }
    }
    suspend fun shareRecipe(recipeId: String): RecipeResponse {
        return myHttpRequestNetworkCall {
            recipeService.shareRecipe(ServiceBuilder.token!!, recipeId  )
        }
    }
    suspend fun postRecipe(recipeId: String): RecipeResponse {
        return myHttpRequestNetworkCall {
            recipeService.postRecipe(ServiceBuilder.token!!, recipeId )
        }
    }
    suspend fun archivedRecipe(recipeId: String): RecipeResponse {
        return myHttpRequestNetworkCall {
            recipeService.archivedRecipe(ServiceBuilder.token!!, recipeId  )
        }
    }

    suspend fun deleteArchived(recipeId: String): RecipeResponse {
        return myHttpRequestNetworkCall {
            recipeService.deleteArchived(ServiceBuilder.token!!, recipeId  )
        }
    }

    suspend fun viewArchivedRecipe(): RecipesResponse {
        return myHttpRequestNetworkCall {
            recipeService.viewArchivedRecipe(ServiceBuilder.token!!)
        }
    }
    suspend fun reportRecipe(recipeId: String,reportReason:String): RecipeResponse {
        return myHttpRequestNetworkCall {
            recipeService.reportRecipe(ServiceBuilder.token!!, recipeId,reportReason)
        }
    }

    suspend fun deleteRecipe(recipeId: String): RecipeResponse {
        return myHttpRequestNetworkCall {
            recipeService.deleteRecipe(ServiceBuilder.token!!, recipeId  )
        }
    }
}