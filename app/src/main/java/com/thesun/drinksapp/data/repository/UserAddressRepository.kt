package com.thesun.drinksapp.data.repository

import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class UserAddressRepository @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    fun getAddressRef(): DatabaseReference {
        return firebaseRepository.getAddressDatabaseReference()

    }
}