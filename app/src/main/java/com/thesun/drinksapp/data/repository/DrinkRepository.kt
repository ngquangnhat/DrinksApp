package com.thesun.drinksapp.data.repository

import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class DrinkRepository @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    fun getDrinkRef(): DatabaseReference {
        return firebaseRepository.getDrinkDatabaseReference()
    }
}
