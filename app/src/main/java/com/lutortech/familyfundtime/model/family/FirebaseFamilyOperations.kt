package com.lutortech.familyfundtime.model.family

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lutortech.familyfundtime.Constants.LOG_TAG
import com.lutortech.familyfundtime.model.family.member.FamilyMember
import com.lutortech.familyfundtime.model.family.member.moneybin.MoneyBin
import com.lutortech.familyfundtime.model.family.member.moneybin.MoneyBin.Companion.MONEYBIN_BALANCE_DEFAULT
import com.lutortech.familyfundtime.model.family.member.moneybin.MoneyBin.Companion.MONEYBIN_NAME_DEFAULT
import com.lutortech.familyfundtime.model.family.member.moneybin.MoneyBin.Companion.MONEYBIN_NOTE_DEFAULT
import com.lutortech.familyfundtime.model.user.User
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.IllegalStateException
import java.time.Instant

class FirebaseFamilyOperations : FamilyOperations {

    private val db = Firebase.firestore
    private val currentFamily: MutableState<Family?> = mutableStateOf(null)

    override suspend fun createFamily(owner: User): Family {

        val familyRef = db.collection(Family.COLLECTION).document()
        val familyMemberRef = db.collection(
            "${familyRef.path}/${FamilyMember.COLLECTION}"
        ).document(owner.id)
        val moneyBinRef = db.collection("${familyMemberRef.path}/${MoneyBin.COLLECTION}").document()

        db.runTransaction { transaction ->

            // Create the new Family
            val familyData = Family.dbDataMap(owner.id)
            transaction.set(familyRef, familyData)

            val ownerData = FamilyMember.dbDataMap(
                owner.id,
                isOwner = true,
                isAdmin = true
            )
            transaction.set(familyMemberRef, ownerData)
            transaction.set(moneyBinRef, defaultMoneyBinData())
        }.await()
        return familyRef.get().await().toFamily().also { currentFamily.value = it }
    }

    private fun DocumentSnapshot.toFamily(): Family {
        val creator = getString(Family.FIELD_CREATOR_USER_ID)
            ?: throw IllegalStateException("No creator id for family [${reference.path}]")
        val createdTimestamp =
            getLong(Family.FIELD_CREATED_TIMESTAMP)?.let { Instant.ofEpochMilli(it) }
                ?: throw IllegalStateException("No created timestamp for family [{reference.path}]")
        return Family(
            id,
            reference.path,
            creator,
            createdTimestamp
        )
    }

    // This is probably not very performant, but also there should usually not be many families
    // per user.
    override suspend fun getFamiliesForUser(user: User): Set<Family> {
        val result =
            db.collectionGroup(FamilyMember.COLLECTION)
                .whereEqualTo(FamilyMember.FIELD_USER_ID, user.id).get().await()
        val familyPaths = result.documents.map { it.reference.parent.parent?.path }

        return familyPaths.filterNotNull()
            .map { path -> coroutineScope { async { db.document(path).get().await().toFamily() } } }
            .awaitAll().toSet()
    }

    override suspend fun getOrCreateFamilyMember(user: User, family: Family): FamilyMember {
        val familyMemberRef = db.collection(
            "${family.url}/${FamilyMember.COLLECTION}"
        ).document(user.id)
        val moneyBinRef = db.collection("${familyMemberRef.path}/${MoneyBin.COLLECTION}").document()

        db.runTransaction { transaction ->
            // Check if this user already exists in this family
            val existingFamilyMember = transaction.get(familyMemberRef)
            if (existingFamilyMember.exists()) {
                Log.i(
                    LOG_TAG,
                    "User [${user.id}] is already a member of family [${family.id}], " +
                            "familyMemberId = [${existingFamilyMember}]"
                )
            } else {
                // add the user to the family by creating a new familymember entry with default data
                val memberData =
                    FamilyMember.dbDataMap(
                        user.id,
                        isOwner = false,
                        isAdmin = false
                    )
                transaction.set(familyMemberRef, memberData)
                transaction.set(moneyBinRef, defaultMoneyBinData())
            }
        }.await()
        return familyMemberRef.get().await().toFamilyMember(user)
    }

    private fun defaultMoneyBinData() =
        MoneyBin.dbDataMap(
            MONEYBIN_NAME_DEFAULT,
            MONEYBIN_NOTE_DEFAULT,
            MONEYBIN_BALANCE_DEFAULT
        )

    private fun getFamilyMemberFromFamily(familyPath: String, user: User) =
        db.collection("$familyPath/${FamilyMember.COLLECTION}")
            .whereEqualTo(FamilyMember.FIELD_USER_ID, user.id)
            .get()
            .result
            .documents
            .firstOrNull()
            ?.toFamilyMember(user)

    override fun currentFamily(): MutableState<Family?> = currentFamily

    companion object {
        fun DocumentSnapshot.toFamilyMember(user: User): FamilyMember {
            val isOwner = this.getBoolean(FamilyMember.FIELD_IS_OWNER)
                ?: throw IllegalStateException("Field IS_OWNER not found for family member [${reference.path}]")
            val isAdmin = this.getBoolean(FamilyMember.FIELD_IS_ADMIN)
                ?: throw IllegalStateException("Field IS_ADMIN not found for family member [${reference.path}]")
            val createdTimestamp = this.getLong(FamilyMember.FIELD_CREATED_TIMESTAMP)
                ?: throw IllegalStateException("Field CREATED_TIMESTAMP not found for family member [${reference.path}]")
            return FamilyMember(
                this.id,
                this.reference.path,
                Instant.ofEpochMilli(createdTimestamp),
                user,
                isOwner,
                isAdmin
            )
        }
    }

}

