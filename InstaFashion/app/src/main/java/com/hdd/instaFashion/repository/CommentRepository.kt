package com.hdd.instaFashion.repository

import com.hdd.instaFashion.data.models.Comment
import com.hdd.instaFashion.data.remoteDataSource.HttpRequestNetworkCall
import com.hdd.instaFashion.data.remoteDataSource.ServiceBuilder
import com.hdd.instaFashion.data.remoteDataSource.response.CommentResponse
import com.hdd.instaFashion.data.remoteDataSource.response.CommentsResponse
import com.hdd.instaFashion.data.remoteDataSource.services.CommentService

class CommentRepository : HttpRequestNetworkCall() {
    private val commentService = ServiceBuilder.buildService(CommentService::class.java)
    suspend fun getAllComment(postId:String): CommentsResponse {
        return myHttpRequestNetworkCall {
            commentService.getAllComment(postId)
        }
    }

    suspend fun deleteComment(postId:String): CommentResponse {
        return myHttpRequestNetworkCall {
            commentService.deleteComment(ServiceBuilder.token!!, postId)
        }
    }

    suspend fun addComment(postId:String,comment: Comment): CommentsResponse {
        return myHttpRequestNetworkCall {
            commentService.addComment(ServiceBuilder.token!!,postId,comment)
        }
    }

    suspend fun editComment(commentId:String,comment: Comment): CommentResponse {
        return myHttpRequestNetworkCall {
            commentService.editComment(ServiceBuilder.token!!,commentId,comment)
        }
    }
}