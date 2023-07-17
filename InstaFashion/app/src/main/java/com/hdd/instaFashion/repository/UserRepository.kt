package com.hdd.instaFashion.repository

import com.hdd.instaFashion.data.models.User
import com.hdd.instaFashion.data.remoteDataSource.HttpRequestNetworkCall
import com.hdd.instaFashion.data.remoteDataSource.ServiceBuilder
import com.hdd.instaFashion.data.remoteDataSource.response.*
import com.hdd.instaFashion.data.remoteDataSource.services.UserServices
import okhttp3.MultipartBody


class UserRepository : HttpRequestNetworkCall() {
    private val userService = ServiceBuilder.buildService(UserServices::class.java)

    suspend fun registerUser(user: User): UserResponse {
        return myHttpRequestNetworkCall {
            userService.registerUser(user)
        }
    }
    suspend fun loginUser(user: User): UserResponse {
        return myHttpRequestNetworkCall {
            userService.login(user)
        }
    }
    suspend fun getUserProfile(): UserResponse {
        return myHttpRequestNetworkCall {
            userService.getUserProfile(ServiceBuilder.token!!)
        }
    }

    suspend fun getOtherUserProfile(id:String): UserResponse {
        return myHttpRequestNetworkCall {
            userService.getOtherUserProfile(ServiceBuilder.token!!,id)
        }
    }
    suspend fun followUser(id:String): UserResponse {
        return myHttpRequestNetworkCall {
            userService.followUser(ServiceBuilder.token!!,id)
        }
    }
    suspend fun getUserPost(): PostsResponse {
        return myHttpRequestNetworkCall {
            userService.getUserPost(ServiceBuilder.token!!)
        }
    }
    suspend fun getUserRecipe(): RecipesResponse {
        return myHttpRequestNetworkCall {
            userService.getUserRecipe(ServiceBuilder.token!!)
        }
    }
    suspend fun updateUserProfile(user: User)
            : UserResponse {
        return myHttpRequestNetworkCall {
            userService.updateUserProfile(ServiceBuilder.token!!,user)
        }
    }

    suspend fun uploadImage(body: MultipartBody.Part)
            : ImageResponse {
        return myHttpRequestNetworkCall {
            userService.uploadImage(ServiceBuilder.token!!, body)
        }
    }

    suspend fun changePassword(user: User)
            : UserResponse {
        return myHttpRequestNetworkCall {
            userService.changePassword(ServiceBuilder.token!!, user)
        }
    }

    suspend fun savedRecipe(recipeId:String): UserResponse {
        return myHttpRequestNetworkCall {
            userService.savedRecipe(ServiceBuilder.token!!,recipeId)
        }
    }
    suspend fun getUserSavedRecipe(): RecipesResponse {
        return myHttpRequestNetworkCall {
            userService.getUserSavedRecipe(ServiceBuilder.token!!)
        }
    }
    suspend fun resetUser(user: User): UserResponse {
        return myHttpRequestNetworkCall {
            userService.resetUser(user)
        }
    }
    suspend fun setNewPassword(user: User): UserResponse {
        return myHttpRequestNetworkCall {
            userService.setNewPassword(user)
        }
    }
    suspend fun validateEmail(user: User): UserResponse {
        return myHttpRequestNetworkCall {
            userService.validateEmail(user)
        }
    }

    suspend fun getUserFollowers(): FollowersFollowingResponse {
        return myHttpRequestNetworkCall {
            userService.getUserFollowers(ServiceBuilder.token!!)
        }
    }
    suspend fun getUserFollowing(): FollowersFollowingResponse {
        return myHttpRequestNetworkCall {
            userService.getUserFollowing(ServiceBuilder.token!!)
        }
    }
}