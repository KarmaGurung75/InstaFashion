package com.hdd.instaFashion.data.models


data class ReviewRating(
    val _id:String="",
    val review:String?=null,
    val rating:Int=0,
    val date: String?=null,
    val recipe:String?=null,
    val user: User?=null,
)
