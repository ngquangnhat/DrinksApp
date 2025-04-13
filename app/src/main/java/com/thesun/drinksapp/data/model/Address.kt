package com.thesun.drinksapp.data.model

data class Address(
    var id: Long = 0,
    var name: String? = null,
    var phone: String? = null,
    var address: String? = null,
    var userEmail: String? = null,
    var isSelected: Boolean = false
)
