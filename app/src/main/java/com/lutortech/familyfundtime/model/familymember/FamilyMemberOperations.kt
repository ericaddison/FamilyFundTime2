package com.lutortech.familyfundtime.model.familymember

import com.lutortech.familyfundtime.model.user.User

interface FamilyMemberOperations {

    suspend fun getFamilyMember(userId: String, familyId: String): FamilyMember?

    suspend fun getOrCreateFamilyMember(user: User, familyId: String): FamilyMember

}