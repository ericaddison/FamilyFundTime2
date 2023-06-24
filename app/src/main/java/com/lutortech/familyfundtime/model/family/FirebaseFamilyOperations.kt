package com.lutortech.familyfundtime.model.family

import android.util.Log
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lutortech.familyfundtime.model.user.User
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.tasks.await

class FirebaseFamilyOperations : FamilyOperations {

    private val db = Firebase.firestore

    override suspend fun createFamily(owner: User): String {

        // Create the new Family
        val familyData = mapOf<String, Any>(
            "creatorUserId" to owner.id()
        )
        val familyDocument = db.collection(COLLECTION_FAMILIES).add(familyData).await()

        val ownerData = mapOf(
            FIELD_USER_ID to owner.id(),
            FIELD_IS_OWNER to true,
            FIELD_IS_ADMIN to true
        )
        db.collection("$COLLECTION_FAMILIES/${familyDocument.id}/$COLLECTION_MEMBERS")
            .document(owner.id()).set(ownerData).await()

        return familyDocument.id
    }

    override suspend fun familyExists(familyId: String): Boolean {
        val doc = db.document("$COLLECTION_FAMILIES/$familyId").get().await()
        return doc.exists()
    }

    override suspend fun getFamiliesForUser(userId: String): Set<String> {
        val result =
            db.collectionGroup(COLLECTION_MEMBERS).whereEqualTo(FIELD_USER_ID, userId).get().await()
        return result.documents.map { it.reference.parent.parent!!.id }.toSet()
    }

    override suspend fun addUserToFamily(userId: String, familyId: String) {
        if (!familyExists(familyId)) {
            throw IllegalArgumentException("Family does not exist: [$familyId]")
        }

        val memberData = mapOf(
            FIELD_USER_ID to userId,
            FIELD_IS_OWNER to false,
            FIELD_IS_ADMIN to false
        )
        db.collection("$COLLECTION_FAMILIES/$familyId/$COLLECTION_MEMBERS").document(userId)
            .set(memberData, SetOptions.mergeFields())
    }

    companion object {
        private const val COLLECTION_FAMILIES = "families"
        private const val COLLECTION_MEMBERS = "members"
        private const val FIELD_USER_ID = "userId"
        private const val FIELD_IS_ADMIN = "isAdmin"
        private const val FIELD_IS_OWNER = "isOwner"
    }

}