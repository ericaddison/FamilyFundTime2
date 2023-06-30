package com.lutortech.familyfundtime.model.invitation

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lutortech.familyfundtime.model.family.Family
import com.lutortech.familyfundtime.model.family.FirebaseFamilyOperations.Companion.toFamily
import com.lutortech.familyfundtime.model.user.FirebaseUserOperations.Companion.toUser
import com.lutortech.familyfundtime.model.user.User
import kotlinx.coroutines.tasks.await
import java.time.Instant

class FirebaseInvitationOperations : InvitationOperations {

    private val db = Firebase.firestore

    override suspend fun getInvitationsFromUser(user: User): Set<Invitation> =
        db.collection(Invitation.COLLECTION).whereEqualTo(Invitation.FIELD_FROM_USER_ID, user.id)
            .get().await().documents.map { it.toInvitation() }.toSet()

    override suspend fun getInvitationsToUser(user: User): Set<Invitation> =
        db.collection(Invitation.COLLECTION).whereEqualTo(Invitation.FIELD_TO_USER_ID, user.id)
            .get().await().documents.map { it.toInvitation() }.toSet()

    override suspend fun inviteUserToJoinFamily(
        from: User,
        to: User,
        family: Family,
        note: String?
    ): Invitation {
        val data = Invitation.dbDataMap(from, to, family, note, InvitationStatus.PENDING)
        val invitationRef = db.collection(Invitation.COLLECTION).add(data).await()
        return invitationRef.get().await().toInvitation()
    }

    private suspend fun DocumentSnapshot.toInvitation(): Invitation {
        val fromId = this.getString(Invitation.FIELD_FROM_USER_ID)
            ?: throw IllegalStateException("Field FROM_USER_ID not found for invitation [${reference.path}]")
        val toId = this.getString(Invitation.FIELD_TO_USER_ID)
            ?: throw IllegalStateException("Field TO_USER_ID not found for invitation [${reference.path}]")
        val familyId = this.getString(Invitation.FIELD_FAMILY_ID)
            ?: throw IllegalStateException("Field FAMILY_ID not found for invitation [${reference.path}]")
        val note = this.getString(Invitation.FIELD_NOTE)
            ?: throw IllegalStateException("Field FIELD_NOTE not found for invitation [${reference.path}]")
        val createdTimestamp = this.getLong(Invitation.FIELD_CREATED_TIMESTAMP)
            ?: throw IllegalStateException("Field FIELD_CREATED_TIMESTAMP not found for invitation [${reference.path}]")
        val status = this.getString(Invitation.FIELD_STATUS)
            ?: throw IllegalStateException("Field FIELD_STATUS not found for invitation [${reference.path}]")

        val fromUser = db.document("${User.COLLECTION}/$fromId").get().await().toUser()
        val toUser = db.document("${User.COLLECTION}/$toId").get().await().toUser()
        val family = db.document("${Family.COLLECTION}/$familyId").get().await().toFamily()

        return Invitation(
            id,
            Instant.ofEpochMilli(createdTimestamp),
            fromUser,
            toUser,
            family,
            note,
            InvitationStatus.valueOf(status)
        )
    }
}


