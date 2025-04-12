package com.thesun.drinksapp.data.repository

import com.google.firebase.database.DatabaseReference
import javax.inject.Inject

class RatingReviewRepository @Inject constructor(
    private val firebaseRepository: FirebaseRepository
){
    fun getRatingDrinkRef(drinkId: String): DatabaseReference {
        return firebaseRepository.getRatingDrinkDatabaseReference(drinkId)
    }
    fun getFeedbackRef(): DatabaseReference {
        return firebaseRepository.getFeedbackDatabaseReference()
    }


}