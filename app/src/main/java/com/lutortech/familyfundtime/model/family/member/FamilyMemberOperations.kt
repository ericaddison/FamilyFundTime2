package com.lutortech.familyfundtime.model.family.member

import androidx.compose.runtime.MutableState
import com.lutortech.familyfundtime.model.family.Family

interface FamilyMemberOperations {
    suspend fun getFamilyMembersForFamily(family: Family): Set<FamilyMember>

    fun currentFamilyMembers(): MutableState<Set<FamilyMember>>
}