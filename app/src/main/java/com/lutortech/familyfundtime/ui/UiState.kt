package com.lutortech.familyfundtime.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lutortech.familyfundtime.model.family.Family
import com.lutortech.familyfundtime.model.family.member.FamilyMember
import com.lutortech.familyfundtime.model.family.member.FamilyMemberOperations
import com.lutortech.familyfundtime.model.user.User
import com.lutortech.familyfundtime.model.user.UserOperations
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UiState(userOperations: UserOperations, familyMemberOperations: FamilyMemberOperations) :
    ViewModel() {

    val currentUser: StateFlow<User?> = userOperations.currentUser()
    val isSignedIn: StateFlow<Boolean> = currentUser.map { it != null }
        .stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = false)

    val selectedFamily: MutableState<Family?> = mutableStateOf(null)
    val currentFamilyMembers: MutableState<Set<FamilyMember>> = mutableStateOf(setOf())

    init {
        // Flow to update currentFamilyMembers when selectedFamily changes
        viewModelScope.launch {
            snapshotFlow { selectedFamily.value }.collectLatest {
                currentFamilyMembers.value = it?.let { family ->
                    familyMemberOperations.getFamilyMembersForFamily(family)
                } ?: setOf()
            }
        }

        // Flow to clear selectedFamily when currentUser changes
        viewModelScope.launch {
            currentUser.map { selectedFamily.value = null }.collect()
        }
    }
}