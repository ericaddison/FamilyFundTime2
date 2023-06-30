package com.lutortech.familyfundtime.ui.familymember

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lutortech.familyfundtime.model.family.FamilyOperations
import com.lutortech.familyfundtime.model.family.member.FamilyMember
import com.lutortech.familyfundtime.model.family.member.FamilyMemberOperations
import com.lutortech.familyfundtime.model.family.member.FirebaseFamilyMemberOperations
import com.lutortech.familyfundtime.model.family.member.moneybin.MoneyBin
import com.lutortech.familyfundtime.model.family.member.moneybin.MoneyBinOperations
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FamilyMemberListViewModel(
    familyMemberOperations: FamilyMemberOperations,
    familyOperations: FamilyOperations,
    moneyBinOperations: MoneyBinOperations
) : ViewModel() {

    // UI State
    val familyMembers: StateFlow<Set<FamilyMember>> =
        snapshotFlow { familyOperations.currentFamily().value }.map {
            it?.let { realFamily ->
                familyMemberOperations.getFamilyMembersForFamily(realFamily)
            } ?: setOf()
        }.stateIn(viewModelScope, SharingStarted.Lazily, setOf())


    // Event Handlers

}