package com.lutortech.familyfundtime.model.user

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseUserOperations : UserOperations {

    private val db = Firebase.firestore

    override suspend fun storeUser(user: User): Boolean {
        val document = db.document("$COLLECTION_USERS/${user.id()}").get().await()

        // user already exists, return false -> did not store user
        if(document.exists()) {
            return false
        }

        // user does not exist in DB, store it
        db.collection(COLLECTION_USERS).document(user.id()).set(user.toMap()).await()
        return true
    }

    companion object{
        private const val COLLECTION_USERS = "users"
    }

}
