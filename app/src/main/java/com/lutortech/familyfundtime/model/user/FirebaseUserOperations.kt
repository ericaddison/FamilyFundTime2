package com.lutortech.familyfundtime.model.user

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


class FirebaseUserOperations : UserOperations {

    private val db = Firebase.firestore

    override suspend fun storeUser(user: User): Boolean {
        val document = db.document("${User.COLLECTION}/${user.id}").get().await()

        // user already exists, return false -> did not store user
        if (document.exists()) {
            return false
        }

        // user does not exist in DB, store it
        db.collection(User.COLLECTION).document(user.id).set(user.toMap()).await()
        return true
    }

    override suspend fun getAllUsers(): Set<User> =
        db.collection(User.COLLECTION).get().await().map { it.toUser() }.toSet()

    private fun User.toMap(): Map<String, String?> = mapOf(
        User.FIELD_DISPLAY_NAME to displayName,
        User.FIELD_EMAIL to email
    )

    private fun QueryDocumentSnapshot.toUser(): User = User(
        this.id,
        getString(User.FIELD_DISPLAY_NAME),
        getString(User.FIELD_EMAIL)
    )

}
