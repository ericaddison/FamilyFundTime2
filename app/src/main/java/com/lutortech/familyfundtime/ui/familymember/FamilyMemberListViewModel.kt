package com.lutortech.familyfundtime.ui.familymember

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lutortech.familyfundtime.model.family.FamilyOperations
import com.lutortech.familyfundtime.model.family.member.FamilyMember
import com.lutortech.familyfundtime.model.family.member.FamilyMemberOperations
import com.lutortech.familyfundtime.model.family.member.moneybin.MoneyBinOperations
import com.lutortech.familyfundtime.ui.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class FamilyMemberListViewModel(
    uiState: UiState,
    private val moneyBinOperations: MoneyBinOperations
) : ViewModel() {

    // UI State
    val isSignedIn: Flow<Boolean> = uiState.isSignedIn
    val familyMembers: MutableState<Set<FamilyMember>> = uiState.currentFamilyMembers

    // Event Handlers

    // Other Functions
    fun familyMemberCardViewModel(familyMember: FamilyMember): FamilyMemberCardViewModel {
        return FamilyMemberCardViewModel(familyMember, moneyBinOperations)
    }
}