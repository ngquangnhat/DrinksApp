package com.thesun.drinksapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RatingReview(val type: Int, val id: String) : Parcelable {
    companion object {
        const val TYPE_RATING_REVIEW_DRINK = 1
        const val TYPE_RATING_REVIEW_ORDER = 2
    }
}