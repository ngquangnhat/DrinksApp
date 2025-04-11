package com.thesun.drinksapp.data.repository

import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class DetailDrinkRepository @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    fun getDetailDrinkRef(id: Long): DatabaseReference {
        return firebaseRepository.getDrinkDetailDatabaseReference(id)
    }

    fun getToppingRef(): DatabaseReference {
        return firebaseRepository.getToppingDatabaseReference()
    }

}