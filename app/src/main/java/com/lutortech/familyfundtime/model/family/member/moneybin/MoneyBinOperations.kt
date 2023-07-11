package com.lutortech.familyfundtime.model.family.member.moneybin

import com.lutortech.familyfundtime.model.family.member.FamilyMember

interface MoneyBinOperations {

    suspend fun getMoneyBinsForFamilyMember(familyMember: FamilyMember): Set<MoneyBin>
}