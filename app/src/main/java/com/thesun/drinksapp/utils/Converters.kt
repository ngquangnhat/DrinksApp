package com.thesun.drinksapp.utils

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.thesun.drinksapp.data.model.Address
import com.thesun.drinksapp.data.model.Drink
import com.thesun.drinksapp.data.model.PaymentMethod
import com.thesun.drinksapp.data.model.Voucher

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromDrinkList(drinks: List<Drink>): String {
        return gson.toJson(drinks).also {
            Log.d("Converters", "Serialized Drink List: $it")
        }
    }

    @TypeConverter
    fun toDrinkList(json: String): List<Drink> {
        return gson.fromJson(json, Array<Drink>::class.java).toList().also {
            Log.d("Converters", "Deserialized Drink List: $it")
        }
    }

    @TypeConverter
    fun fromPaymentMethod(paymentMethod: PaymentMethod?): String? {
        return paymentMethod?.let { gson.toJson(it) }.also {
            Log.d("Converters", "Serialized PaymentMethod: $it")
        }
    }

    @TypeConverter
    fun toPaymentMethod(json: String?): PaymentMethod? {
        return json?.let { gson.fromJson(it, PaymentMethod::class.java) }.also {
            Log.d("Converters", "Deserialized PaymentMethod: $it")
        }
    }

    @TypeConverter
    fun fromAddress(address: Address?): String? {
        return address?.let { gson.toJson(it) }.also {
            Log.d("Converters", "Serialized Address: $it")
        }
    }

    @TypeConverter
    fun toAddress(json: String?): Address? {
        return json?.let { gson.fromJson(it, Address::class.java) }.also {
            Log.d("Converters", "Deserialized Address: $it")
        }
    }

    @TypeConverter
    fun fromVoucher(voucher: Voucher?): String? {
        return voucher?.let { gson.toJson(it) }.also {
            Log.d("Converters", "Serialized Voucher: $it")
        }
    }

    @TypeConverter
    fun toVoucher(json: String?): Voucher? {
        return json?.let { gson.fromJson(it, Voucher::class.java) }.also {
            Log.d("Converters", "Deserialized Voucher: $it")
        }
    }
}