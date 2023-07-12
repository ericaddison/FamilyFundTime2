package com.lutortech.familyfundtime.model.family

import androidx.compose.runtime.MutableState
import com.lutortech.familyfundtime.model.family.member.FamilyMember
import com.lutortech.familyfundtime.model.user.User

interface FamilyOperations {

    /** Creates a new Family entry and returns the Family Id. */
    suspend fun createFamily(owner: User): Family

    /** Returns the ids of the Families user is a part of */
    suspend fun getFamiliesForUser(user: User): Set<Family>

    suspend fun getOrCreateUserBackedFamilyMember(user: User, family: Family): FamilyMember

    suspend fun getFamily(familyId: String?): Family?
}