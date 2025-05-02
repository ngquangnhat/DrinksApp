package com.thesun.drinksapp.data.model

data class Feedback(
    var name: String? = null,
    var phone: String? = null,
    var email: String? = null,
    var userName: String? = null,
    val profilePictureUrl: String? = null,
    var comment: String? = null
)