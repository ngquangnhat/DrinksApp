package com.thesun.drinksapp.data.remote

import com.thesun.drinksapp.data.model.FcmMessage
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface FcmApiService {
    @Headers("Content-Type: application/json", "Authorization: key=YOUR_SERVER_KEY")
    @POST("fcm/send")
    fun sendNotification(@Body message: FcmMessage): Call<Void>
}