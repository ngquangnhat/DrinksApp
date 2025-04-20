package com.thesun.drinksapp.data.model

data class Contact(var id: Int, var image: Int) {
    companion object {
        const val FACEBOOK = 0
        const val HOTLINE = 1
        const val GMAIL = 2
        const val SKYPE = 3
        const val YOUTUBE = 4
        const val ZALO = 5
    }

    fun getTypeName(): String = when (id) {
        FACEBOOK -> "Facebook"
        HOTLINE -> "Hotline"
        GMAIL -> "Gmail"
        SKYPE -> "Skype"
        YOUTUBE -> "Youtube"
        ZALO -> "Zalo"
        else -> ""
    }
}