package com.thesun.drinksapp.data.model

data class DrinkOrder(
    var name: String? = null,
    var option: String? = null,
    var count: Int = 0,
    var price: Int = 0,
    var image: String? = null
)