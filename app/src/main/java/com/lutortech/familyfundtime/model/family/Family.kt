package com.lutortech.familyfundtime.model.family

import com.lutortech.familyfundtime.model.family.member.FamilyMember

data class Family(val id: String, val familyMembers: Set<FamilyMember>) {

    companion object {
        const val COLLECTION = "families"
    }
}