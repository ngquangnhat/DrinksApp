package com.thesun.drinksapp.data.model

data class PaymentMethod(
    var id: Int = 0,
    var name: String? = null,
    var description: String? = null,
    var isSelected: Boolean = false,
)
