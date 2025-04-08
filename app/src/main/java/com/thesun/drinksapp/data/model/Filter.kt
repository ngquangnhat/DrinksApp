package com.thesun.drinksapp.data.model

data class Filter(
    var id: Int = 0,
    var name: String? = null,
    var isSelected: Boolean = false
) {
    companion object {
        const val TYPE_FILTER_ALL = 1
        const val TYPE_FILTER_RATE = 2
        const val TYPE_FILTER_PRICE = 3
        const val TYPE_FILTER_PROMOTION = 4
    }
}