package com.lutortech.familyfundtime.model.familymember

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lutortech.familyfundtime.Constants.LOG_TAG
import com.lutortech.familyfundtime.model.user.User
import kotlinx.coroutines.tasks.await

class FirebaseFamilyMemberOperations : FamilyMemberOperations {

    val db = Firebase.firestore

    override suspend fun getFamilyMember(userId: String, familyId: String): FamilyMember? {
        val documents = db.collection("familyMembers").whereEqualTo("userId", userId)
            .whereEqualTo("familyId", familyId).get().await()

        Log.i(LOG_TAG, "Got document $documents")
        Log.i(LOG_TAG, "size? ${documents.size()}")
        Log.i(LOG_TAG, "id? ${documents.map { it.id }}")

        return documents.firstOrNull()?.let{
            FamilyMember(userId, it.getString("name"), familyId)
        }
    }

    override suspend fun getOrCreateFamilyMember(user: User, familyId: String): FamilyMember =
        getFamilyMember(user.id(), familyId) ?: hashMapOf(
            "userId" to user.id(),
            "name" to user.displayName(),
            "familyId" to familyId
        ).let {
            db.collection("familyMembers").document(user.id()).set(it)
            Log.i(LOG_TAG, "Created new Family Member in Firestore [${user.id()}]")
            FamilyMember(
                user.id(),
                user.displayName(),
                familyId
            )
        }
}
