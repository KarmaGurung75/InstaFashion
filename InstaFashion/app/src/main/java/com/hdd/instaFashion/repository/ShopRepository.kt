package com.hdd.instaFashion.repository

import com.hdd.instaFashion.data.models.Restaurant
import com.hdd.instaFashion.data.remoteDataSource.HttpRequestNetworkCall
import com.hdd.instaFashion.data.remoteDataSource.ServiceBuilder
import com.hdd.instaFashion.data.remoteDataSource.response.ImageResponse
import com.hdd.instaFashion.data.remoteDataSource.response.RestaurantResponse
import com.hdd.instaFashion.data.remoteDataSource.services.RestaurantServices
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ShopRepository : HttpRequestNetworkCall() {
    private val restaurantService = ServiceBuilder.buildService(RestaurantServices::class.java)

    suspend fun addRestaurantDetail(image: MultipartBody.Part,restaurantName: RequestBody, restaurantDescription: RequestBody,
                                    restaurantAddress: RequestBody,restaurantOpeningTime: RequestBody,restaurantClosingTime: RequestBody,
                                    restaurantPhone: RequestBody,): RestaurantResponse {
        return myHttpRequestNetworkCall {
            restaurantService.addRestaurantDetail(ServiceBuilder.token!!,image,restaurantName, restaurantDescription,restaurantAddress,restaurantOpeningTime,restaurantClosingTime,restaurantPhone)
        }
    }

    suspend fun getRestaurantById(restaurantId: String): RestaurantResponse {
        return myHttpRequestNetworkCall {
            restaurantService.getRestaurantById(ServiceBuilder.token!!,restaurantId)
        }
    }

    suspend fun updateRestaurantDetails(restaurantId: String,restaurant: Restaurant)
            : RestaurantResponse {
        return myHttpRequestNetworkCall {
            restaurantService.updateRestaurantDetails(ServiceBuilder.token!!,restaurantId,restaurant)
        }
    }

    suspend fun updateRestaurantImage(restaurantId: String,body: MultipartBody.Part)
            : ImageResponse {
        return myHttpRequestNetworkCall {
            restaurantService.updateRestaurantImage(ServiceBuilder.token!!,restaurantId,body)
        }
    }

    suspend fun updateRestaurantCoverImage(restaurantId: String,body: MultipartBody.Part)
            : ImageResponse {
        return myHttpRequestNetworkCall {
            restaurantService.updateRestaurantCoverImage(ServiceBuilder.token!!,restaurantId,body)
        }
    }

    suspend fun updateRestaurantDetailsWithoutImage(restaurantId: String,restaurant: Restaurant): RestaurantResponse {
        return myHttpRequestNetworkCall {
            restaurantService.updateRestaurantDetailsWithoutImage(ServiceBuilder.token!!,restaurantId, restaurant)
        }
    }

    suspend fun deleteRestaurantDetails(restaurantId: String): RestaurantResponse {
        return myHttpRequestNetworkCall {
            restaurantService.deleteRestaurantDetails(ServiceBuilder.token!!,restaurantId)
        }
    }
}