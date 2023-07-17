package com.hdd.instaFashion.data.models

data class Search(
    var pattern: String? = null,
    var user: MutableList<User>? = null,
    var recipe : MutableList<Recipe>? = null,
)