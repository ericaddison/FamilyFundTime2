package com.lutortech.familyfundtime.ui

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lutortech.familyfundtime.Constants.LOG_TAG
import com.lutortech.familyfundtime.model.family.Family
import com.lutortech.familyfundtime.model.family.FamilyOperations
import com.lutortech.familyfundtime.model.family.member.FamilyMember
import com.lutortech.familyfundtime.model.family.member.FamilyMemberOperations
import com.lutortech.familyfundtime.model.user.User
import com.lutortech.familyfundtime.model.user.UserOperations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UiState(
    userOperations: UserOperations,
    private val familyOperations: FamilyOperations,
    familyMemberOperations: FamilyMemberOperations
) :
    ViewModel() {

    // private flows
    private val _selectedFamily: MutableStateFlow<Family?> = MutableStateFlow(null)

    // UI StateFlows
    val currentUser: StateFlow<User?> = userOperations.currentUser()
    val isSignedIn: StateFlow<Boolean> = currentUser.map { it != null }
        .stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = false)
    val selectedFamily: StateFlow<Family?> = _selectedFamily
    val currentFamilyMembers: StateFlow<Set<FamilyMember>> = selectedFamily.map {
        Log.d(
            LOG_TAG,
            "Selected Family changed to [${it?.url ?: "NULL"}], loading new family members"
        )
        it?.let { realFamily -> familyMemberOperations.getFamilyMembersForFamily(realFamily) }
            ?: setOf()
    }.stateIn(scope = viewModelScope, started = SharingStarted.Lazily, initialValue = setOf())

    init {
        // Flow to clear selectedFamily when currentUser changes
        viewModelScope.launch {
            // initial value
            fetchAndSetSelectedFamily(currentUser.value)
            currentUser.map {
                fetchAndSetSelectedFamily(it)
            }.collect()
        }
    }

    private suspend fun fetchAndSetSelectedFamily(user: User?) {
        val family =
            user?.let { realUser -> familyOperations.getFamily(realUser.lastSelectedFamilyId) }
        setSelectedFamily(family)
    }

    fun setSelectedFamily(family: Family?) {
        _selectedFamily.value = family
    }
}