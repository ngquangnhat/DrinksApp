package com.thesun.drinksapp.data.repository

import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    fun getCategoryRef(): DatabaseReference {
        return firebaseRepository.getCategoryDatabaseReference()
    }
}
