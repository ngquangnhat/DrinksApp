package com.thesun.drinksapp.data.model

data class Topping (
    var id: Long = 0,
    var name: String? = null,
    var price: Int = 0,
    var isSelected: Boolean = false
){
    companion object {
        const val VARIANT_ICE = "Đá"
        const val VARIANT_HOT = "Nóng"
        const val SIZE_REGULAR = "Nhỏ"
        const val SIZE_MEDIUM = "Vừa"
        const val SIZE_LARGE = "Lớn"
        const val SUGAR_NORMAL = "Bình thường"
        const val SUGAR_LESS = "Giảm bớt"
        const val ICE_NORMAL = "Bình thường"
        const val ICE_LESS = "Giảm bớt"
    }
}