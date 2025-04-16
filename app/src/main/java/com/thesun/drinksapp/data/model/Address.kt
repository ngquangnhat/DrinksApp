package com.thesun.drinksapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    var id: Long = 0,
    var name: String? = null,
    var phone: String? = null,
    var address: String? = null,
    var userEmail: String? = null,
    var isSelected: Boolean = false
) : Parcelable
