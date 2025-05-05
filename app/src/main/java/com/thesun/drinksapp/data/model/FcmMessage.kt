package com.thesun.drinksapp.data.model

import com.google.gson.annotations.SerializedName

data class FcmMessage(
    @SerializedName("to") val to: String,
    @SerializedName("notification") val notification: Notification,
    @SerializedName("data") val data: Map<String, String>?
)

data class Notification(
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String
)