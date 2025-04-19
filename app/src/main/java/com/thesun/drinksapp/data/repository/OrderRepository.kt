package com.thesun.drinksapp.data.repository

import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class OrderRepository @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    fun getOrderPaymentRef() : DatabaseReference {
        return firebaseRepository.getOrderDatabaseReference()
    }
    fun getOrderDetailDatabaseRef(orderId: Long) : DatabaseReference {
        return firebaseRepository.getOrderDetailDatabaseReference(orderId)
    }

    fun getAllOrderDatabaseRef() : DatabaseReference {
        return firebaseRepository.getOrderDatabaseReference()
    }

}