package com.thesun.drinksapp.data.model

import com.google.gson.Gson

data class User(
    var email: String? = null,
    var password: String? = null,
    var isAdmin: Boolean = false,
    val userName: String? = null,
    val profilePictureUrl: String? = null
) {
    fun toJSon(): String {
        val gson = Gson()
        return gson.toJson(this)
    }
}