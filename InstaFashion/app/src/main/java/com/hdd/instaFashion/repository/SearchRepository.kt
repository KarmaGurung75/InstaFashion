package com.hdd.instaFashion.repository

import com.hdd.instaFashion.data.models.Search
import com.hdd.instaFashion.data.remoteDataSource.HttpRequestNetworkCall
import com.hdd.instaFashion.data.remoteDataSource.ServiceBuilder
import com.hdd.instaFashion.data.remoteDataSource.response.SearchResponse
import com.hdd.instaFashion.data.remoteDataSource.services.SearchServices


class SearchRepository : HttpRequestNetworkCall() {
    private val searchService = ServiceBuilder.buildService(SearchServices::class.java)
    
    suspend fun search(search: Search): SearchResponse {
        return myHttpRequestNetworkCall {
            searchService.search(search)
        }
    }
}