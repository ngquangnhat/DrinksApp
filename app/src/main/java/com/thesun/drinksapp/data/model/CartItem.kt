package com.thesun.drinksapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_item")
data class CartItem(
    @PrimaryKey(autoGenerate = true)
    val cartItemId: Long = 0,
    val drinkId: Long,
    val name: String? = null,
    val priceOneDrink: Int = 0,
    val count: Int = 1,
    val totalPrice: Int = 0,
    val option: String? = null,
    val image: String? = null,
    val variant: String? = null,
    val size: String? = null,
    val sugar: String? = null,
    val ice: String? = null,
    val toppingIds: String? = null,
    val note: String? = null
)
