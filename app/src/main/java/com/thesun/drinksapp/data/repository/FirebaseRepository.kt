package com.thesun.drinksapp.data.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class FirebaseRepository {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_URL)

    fun getVoucherDatabaseReference(): DatabaseReference {
        return firebaseDatabase.getReference("voucher")
    }

    fun getAddressDatabaseReference(): DatabaseReference {
        return firebaseDatabase.getReference("address")
    }

    fun getCategoryDatabaseReference(): DatabaseReference {
        return firebaseDatabase.getReference("category")
    }

    fun getDrinkDatabaseReference(): DatabaseReference {
        return firebaseDatabase.getReference("drink")
    }

    fun getDrinkDetailDatabaseReference(drinkId: Long): DatabaseReference {
        return firebaseDatabase.getReference("drink/$drinkId")
    }

    fun getToppingDatabaseReference(): DatabaseReference {
        return firebaseDatabase.getReference("topping")
    }

    fun getFeedbackDatabaseReference(): DatabaseReference {
        return firebaseDatabase.getReference("/feedback")
    }

    fun getOrderDatabaseReference(): DatabaseReference {
        return firebaseDatabase.getReference("order")
    }

    fun getRatingDrinkDatabaseReference(drinkId: String): DatabaseReference {
        return firebaseDatabase.getReference("/drink/$drinkId/rating")
    }

    fun getOrderDetailDatabaseReference(orderId: Long): DatabaseReference {
        return firebaseDatabase.getReference("order/$orderId")
    }
    fun getUserDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference("users")
    }

    companion object {
        private const val FIREBASE_URL = "https://drinksapp-39163-default-rtdb.firebaseio.com/"
    }
}
