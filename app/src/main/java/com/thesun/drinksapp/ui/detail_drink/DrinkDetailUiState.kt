package com.thesun.drinksapp.ui.detail_drink

import com.thesun.drinksapp.data.model.Drink
import com.thesun.drinksapp.data.model.Topping

data class DrinkDetailUiState(
    val drinkId: Long? = null,
    val toppings: List<Topping> = emptyList(),
    val quantity: Int = 1,
    val variant: String = Topping.VARIANT_ICE,
    val size: String = Topping.SIZE_REGULAR,
    val sugar: String = Topping.SUGAR_NORMAL,
    val ice: String = Topping.ICE_NORMAL,
    val notes: String = "",
    val totalPrice: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)