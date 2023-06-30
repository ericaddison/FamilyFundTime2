package com.lutortech.familyfundtime.model.invitation

import com.lutortech.familyfundtime.model.family.Family
import com.lutortech.familyfundtime.model.user.User

interface InvitationOperations {

    suspend fun getInvitationsFromUser(user: User): Set<Invitation>

    suspend fun getInvitationsToUser(user: User): Set<Invitation>

    suspend fun inviteUserToJoinFamily(from: User, to: User, family: Family, note: String?): Invitation
}