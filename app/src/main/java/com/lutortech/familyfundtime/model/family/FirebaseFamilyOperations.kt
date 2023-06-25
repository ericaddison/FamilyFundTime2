package com.lutortech.familyfundtime.model.family

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lutortech.familyfundtime.Constants.LOG_TAG
import com.lutortech.familyfundtime.model.family.member.FamilyMember
import com.lutortech.familyfundtime.model.family.member.moneybin.MoneyBin
import com.lutortech.familyfundtime.model.user.User
import kotlinx.coroutines.tasks.await
import java.lang.IllegalStateException
import java.time.Instant

class FirebaseFamilyOperations : FamilyOperations {

    private val db = Firebase.firestore

    override suspend fun createFamily(owner: User): String {

        // Create the new Family
        val familyData = Family.dbDataMap(owner.id)
        val familyDocument = db.collection(Family.COLLECTION).add(familyData).await()

        val ownerData = FamilyMember.dbDataMap(owner.id, isOwner = true, isAdmin = true)
        val familyMember =
            db.collection("${Family.COLLECTION}/${familyDocument.id}/${FamilyMember.COLLECTION}")
                .add(ownerData).await()

        createDefaultMoneyBin(familyDocument.id, familyMember.id)

        return familyDocument.id
    }

    override suspend fun familyExists(familyId: String): Boolean {
        val doc = db.document("${Family.COLLECTION}/$familyId").get().await()
        return doc.exists()
    }

    override suspend fun getFamiliesForUser(userId: String): Set<String> {
        val result =
            db.collectionGroup(FamilyMember.COLLECTION)
                .whereEqualTo(FamilyMember.FIELD_USER_ID, userId).get().await()
        return result.documents.map { it.reference.parent.parent!!.id }.toSet()
    }

    override suspend fun addUserToFamily(userId: String, familyId: String): FamilyMember {
        if (!familyExists(familyId)) {
            throw IllegalArgumentException("Family does not exist: [$familyId]")
        }

        // Check if this user already exists in this family
        val familyMember = getFamilyMemberFromFamily(familyId, userId)
        if (familyMember != null) {
            Log.i(
                LOG_TAG,
                "User [$userId] is already a member of family [$familyId], familyMemberId = [${familyMember.id}]"
            )
            return familyMember
        }

        // add the user to the family by creating a new familymember entry with default data
        val memberData = FamilyMember.dbDataMap(userId, isOwner = false, isAdmin = false)
        val newFamilyMember =
            db.collection("${Family.COLLECTION}/$familyId/${FamilyMember.COLLECTION}")
                .add(memberData).await()

        // create a default money bin for the user
        createDefaultMoneyBin(familyId, newFamilyMember.id)

        return newFamilyMember.get().await().toFamilyMember()
            ?: throw IllegalStateException("Failed to retrieve newly created family member")
    }

    private suspend fun createDefaultMoneyBin(familyId: String, familyMemberId: String) {
        val moneyBinData = MoneyBin.dbDataMap(
            MONEYBIN_NAME_DEFAULT,
            MONEYBIN_NOTE_DEFAULT,
            MONEYBIN_BALANCE_DEFAULT
        )
        db.collection("${Family.COLLECTION}/$familyId/${FamilyMember.COLLECTION}/${familyMemberId}/${MoneyBin.COLLECTION}")
            .add(moneyBinData).await()
    }

    private suspend fun getFamilyMemberFromFamily(familyId: String, userId: String) =
        db.collection("${Family.COLLECTION}/$familyId/${FamilyMember.COLLECTION}")
            .whereEqualTo(FamilyMember.FIELD_USER_ID, userId)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.toFamilyMember()


    private fun DocumentSnapshot.toFamilyMember(): FamilyMember? {
        val userId = this.getString(FamilyMember.FIELD_USER_ID) ?: return null
        val isOwner = this.getBoolean(FamilyMember.FIELD_IS_OWNER) ?: return null
        val isAdmin = this.getBoolean(FamilyMember.FIELD_IS_ADMIN) ?: return null
        val createdTimestamp = this.getLong(FamilyMember.FIELD_CREATED_TIMESTAMP) ?: return null
        return FamilyMember(
            this.id,
            userId,
            isOwner,
            isAdmin,
            Instant.ofEpochMilli(createdTimestamp)
        )
    }

    companion object {
        private const val MONEYBIN_NAME_DEFAULT = "main"
        private const val MONEYBIN_NOTE_DEFAULT = "This is your main MoneyBin!"
        private const val MONEYBIN_BALANCE_DEFAULT = 0.0
    }

}
