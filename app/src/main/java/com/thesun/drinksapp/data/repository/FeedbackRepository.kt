package com.thesun.drinksapp.data.repository

import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class FeedbackRepository @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {
    fun getFeedbackRef(): DatabaseReference {
        return firebaseRepository.getFeedbackDatabaseReference()

    }
}