package com.lutortech.familyfundtime.model.family.member

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lutortech.familyfundtime.model.family.Family
import com.lutortech.familyfundtime.model.family.FirebaseFamilyOperations.Companion.toFamilyMember
import com.lutortech.familyfundtime.model.user.FirebaseUserOperations.Companion.toUser
import com.lutortech.familyfundtime.model.user.User
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class FirebaseFamilyMemberOperations : FamilyMemberOperations {

    private val db = Firebase.firestore
    private val currentFamilyMembers: MutableState<Set<FamilyMember>> = mutableStateOf(setOf())

    override suspend fun getFamilyMembersForFamily(family: Family): Set<FamilyMember> {
        // get all the family member documents for the family
        val familyMemberCollection =
            db.collection("${family.url}/${FamilyMember.COLLECTION}").get().await()

        // iterate over familyMember documents and transform to data objects
        return familyMemberCollection.documents.map {
            // Get userId
            val userId = it.getString(FamilyMember.FIELD_USER_ID)
                ?: throw IllegalStateException("Field USER_ID not found for family member " +
                        "[${it.reference.path}]")

            // Launch coroutine to get user and create FamilyMember
            coroutineScope {
                async {
                    val userDocument = db.document("${User.COLLECTION}/$userId").get().await()
                    it.toFamilyMember(userDocument.toUser())
                }
            }
        }.awaitAll().toSet()
    }

    override fun currentFamilyMembers(): MutableState<Set<FamilyMember>> = currentFamilyMembers
}