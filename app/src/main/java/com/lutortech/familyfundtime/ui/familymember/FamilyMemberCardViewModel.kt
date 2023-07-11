package com.lutortech.familyfundtime.ui.familymember

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.lutortech.familyfundtime.model.family.member.FamilyMember
import com.lutortech.familyfundtime.model.family.member.moneybin.MoneyBinOperations

class FamilyMemberCardViewModel(
    familyMember: FamilyMember,
    moneyBinOperations: MoneyBinOperations
) {

    // UI State
    val familyMember: MutableState<FamilyMember> = mutableStateOf(familyMember)

    fun profilePicUrl(): Uri? = familyMember.value.user.profilePicUrl

    fun displayName(): String? = familyMember.value.user.displayName
}