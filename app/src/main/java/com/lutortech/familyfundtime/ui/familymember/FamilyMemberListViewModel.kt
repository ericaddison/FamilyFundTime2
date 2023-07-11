package com.lutortech.familyfundtime.ui.familymember

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import com.lutortech.familyfundtime.model.family.member.FamilyMember
import com.lutortech.familyfundtime.model.family.member.moneybin.MoneyBinOperations
import com.lutortech.familyfundtime.ui.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class FamilyMemberListViewModel(
    uiState: UiState,
    private val moneyBinOperations: MoneyBinOperations
) : ViewModel() {

    // UI State
    val isSignedIn: StateFlow<Boolean> = uiState.isSignedIn
    val familyMembers: MutableState<Set<FamilyMember>> = uiState.currentFamilyMembers

    // Event Handlers

    // Other Functions
    fun familyMemberViewModel(familyMember: FamilyMember): FamilyMemberViewModel {
        val familyMemberState = mutableStateOf(familyMembers.value.first { it == familyMember })
        return FamilyMemberViewModel(familyMemberState, moneyBinOperations)
    }
}