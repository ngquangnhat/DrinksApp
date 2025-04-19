package com.thesun.drinksapp.data.model

data class TabOrder(val type: Int, val name: String) {
    companion object {
        const val TAB_ORDER_PROCESS = 1
        const val TAB_ORDER_DONE = 2
    }
}