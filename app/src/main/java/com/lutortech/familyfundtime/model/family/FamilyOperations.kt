package com.lutortech.familyfundtime.model.family

import com.lutortech.familyfundtime.model.familymember.FamilyMember

interface FamilyOperations {

    fun getFamily(familyId: String): Family?

    fun getFamilyMember(userId: String): FamilyMember?

}