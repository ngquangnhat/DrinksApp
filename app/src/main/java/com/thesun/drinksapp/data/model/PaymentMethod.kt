package com.thesun.drinksapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentMethod(
    var id: Int = 0,
    var name: String? = null,
    var description: String? = null,
    var isSelected: Boolean = false,
) : Parcelable
