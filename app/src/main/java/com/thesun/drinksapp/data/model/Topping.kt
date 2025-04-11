package com.thesun.drinksapp.data.model

data class Topping (
    var id: Long = 0,
    var name: String? = null,
    var price: Int = 0,
    var isSelected: Boolean = false
){
    companion object {
        const val VARIANT_ICE = "variant_ice"
        const val VARIANT_HOT = "variant_hot"
        const val SIZE_REGULAR = "size_regular"
        const val SIZE_MEDIUM = "size_medium"
        const val SIZE_LARGE = "size_large"
        const val SUGAR_NORMAL = "sugar_normal"
        const val SUGAR_LESS = "sugar_less"
        const val ICE_NORMAL = "ice_normal"
        const val ICE_LESS = "ice_less"
    }
}