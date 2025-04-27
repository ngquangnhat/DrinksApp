package com.thesun.drinksapp.data.repository

import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
import java.io.File
import javax.inject.Inject

class DrinkRepository @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val storageReference: StorageReference
) {

    fun getDrinkRef(): DatabaseReference {
        return firebaseRepository.getDrinkDatabaseReference()
    }

    fun uploadImage(imagePath: String, imageName: String, onComplete: (String?, Exception?) -> Unit) {
        try {
            if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
                onComplete(imagePath, null)
            } else if (imagePath.startsWith("content://")) {
                val uri = android.net.Uri.parse(imagePath)
                val imageRef = storageReference.child("$imageName.jpg")
                imageRef.putFile(uri)
                    .continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let { throw it }
                        }
                        imageRef.downloadUrl
                    }
                    .addOnSuccessListener { downloadUri ->
                        onComplete(downloadUri.toString(), null)
                    }
                    .addOnFailureListener { exception ->
                        onComplete(null, exception)
                    }
            } else {
                val file = java.io.File(imagePath)
                if (!file.exists()) {
                    onComplete(null, Exception("File does not exist"))
                    return
                }
                val imageRef = storageReference.child("$imageName.jpg")
                imageRef.putFile(android.net.Uri.fromFile(file))
                    .continueWithTask { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let { throw it }
                        }
                        imageRef.downloadUrl
                    }
                    .addOnSuccessListener { uri ->
                        onComplete(uri.toString(), null)
                    }
                    .addOnFailureListener { exception ->
                        onComplete(null, exception)
                    }
            }
        } catch (e: Exception) {
            onComplete(null, e)
        }
    }

    fun deleteImage(imageUrl: String, onComplete: (Exception?) -> Unit) {
        if (imageUrl.contains("firebasestorage.googleapis.com")) {
            try {
                val imageRef = storageReference.storage.getReferenceFromUrl(imageUrl)
                imageRef.delete()
                    .addOnSuccessListener {
                        onComplete(null)
                    }
                    .addOnFailureListener { exception ->
                        onComplete(exception)
                    }
            } catch (e: Exception) {
                onComplete(e)
            }
        } else {
            onComplete(null)
        }
    }
}