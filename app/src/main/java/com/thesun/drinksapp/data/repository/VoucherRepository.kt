package com.thesun.drinksapp.data.repository

import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class VoucherRepository @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    fun getVoucherRef(): DatabaseReference {
        return firebaseRepository.getVoucherDatabaseReference()

    }
}