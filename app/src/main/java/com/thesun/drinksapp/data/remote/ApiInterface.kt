package com.thesun.drinksapp.data.remote

import com.thesun.drinksapp.data.model.CustomerModel
import com.thesun.drinksapp.data.model.EphemeralKeyModel
import com.thesun.drinksapp.data.model.PaymentIntentModel
import com.thesun.drinksapp.utils.Utils.SECRET_KEY
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {

    @Headers("Authorization: Bearer $SECRET_KEY")
    @POST("v1/customers")
    suspend fun getCustomer(): Response<CustomerModel>

    @Headers("Authorization: Bearer $SECRET_KEY",
        "Stripe-Version: 2025-04-30.basil")
    @POST("v1/ephemeral_keys")
    suspend fun getEphemeralKey(
        @Query("customer") customer: String
    ): Response<EphemeralKeyModel>

    @Headers("Authorization: Bearer $SECRET_KEY")
    @POST("v1/payment_intents")
    suspend fun getPaymentIntent(
        @Query("customer") customer: String,
        @Query("amount") amount: String = "100",
        @Query("currency") currency: String = "inr",
        @Query("automatic_payment_methods[enabled]") automaticPay: Boolean = true

    ): Response<PaymentIntentModel>
}