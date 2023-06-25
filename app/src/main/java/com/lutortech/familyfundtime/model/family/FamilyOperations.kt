package com.lutortech.familyfundtime.model.family

import com.lutortech.familyfundtime.model.family.member.FamilyMember
import com.lutortech.familyfundtime.model.user.User

interface FamilyOperations {

    /** Creates a new Family entry and returns the Family Id. */
    suspend fun createFamily(owner: User): String

    /** Gets a Family for the given Id. */
    suspend fun familyExists(familyId: String): Boolean

    /** Returns the ids of the Families user is a part of */
    suspend fun getFamiliesForUser(userId: String): Set<String>

    suspend fun addUserToFamily(userId: String, familyId: String): FamilyMember
}