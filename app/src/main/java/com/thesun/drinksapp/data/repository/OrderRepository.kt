package com.thesun.drinksapp.data.repository

import android.util.Log
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
    fun saveUserFcmToken(userEmail: String, token: String) {
        firebaseRepository.getUserDatabaseReference()
            .child(userEmail.replace(".", "_"))
            .setValue(token)
            .addOnSuccessListener {
                Log.d("FCM", "Token lưu thành công cho $userEmail")
            }
            .addOnFailureListener { e ->
                Log.e("FCM", "Lỗi lưu token: ${e.message}")
            }
    }
    fun getUserFcmToken(userEmail: String, callback: (String?) -> Unit) {
        firebaseRepository.getUserDatabaseReference()
            .child(userEmail.replace(".", "_"))
            .child("fcmToken")
            .get()
            .addOnSuccessListener { snapshot ->
                callback(snapshot.getValue(String::class.java))
            }
            .addOnFailureListener {
                callback(null)
            }
    }

}