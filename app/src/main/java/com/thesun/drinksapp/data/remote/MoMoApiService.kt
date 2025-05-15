package com.thesun.drinksapp.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Headers
import retrofit2.Call

interface MoMoApiService {
    @Headers("Content-Type: application/json")
    @POST("/v2/gateway/api/create")
    fun createPayment(@Body request: MoMoPaymentRequest): Call<MoMoPaymentResponse>
}

data class MoMoPaymentRequest(
    val partnerCode: String,
    val requestId: String,
    val amount: Long,
    val orderId: String,
    val orderInfo: String,
    val redirectUrl: String,
    val ipnUrl: String,
    val requestType: String,
    val signature: String,
    val lang: String,
    val extraData: String = ""
)

data class MoMoPaymentResponse(
    val requestId: String,
    val errorCode: Int,
    val orderId: String,
    val message: String,
    val payUrl: String?,
    val signature: String
)