package com.lutortech.familyfundtime.model.user

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


class FirebaseUserOperations : UserOperations {

    private val db = Firebase.firestore

    override suspend fun storeUser(user: User): Boolean {
        val document = db.document("$COLLECTION_USERS/${user.id}").get().await()

        // user already exists, return false -> did not store user
        if(document.exists()) {
            return false
        }

        // user does not exist in DB, store it
        db.collection(COLLECTION_USERS).document(user.id).set(user.toMap()).await()
        return true
    }

    override suspend fun getAllUsers(): Set<User> =
        db.collection(COLLECTION_USERS).get().await().map { it.toUser() }.toSet()

    companion object{
        private const val COLLECTION_USERS = "users"

        private const val FIELD_ID = "id"
        private const val FIELD_DISPLAY_NAME = "displayName"
        private const val FIELD_EMAIL = "email"
    }

    private fun User.toMap(): Map<String, String?> = mapOf(
        FIELD_ID to id,
        FIELD_DISPLAY_NAME to displayName,
        FIELD_EMAIL to email
    )

    private fun QueryDocumentSnapshot.toUser(): User = User(getString(FIELD_ID)!!, getString(
        FIELD_DISPLAY_NAME), getString(FIELD_EMAIL))

}
