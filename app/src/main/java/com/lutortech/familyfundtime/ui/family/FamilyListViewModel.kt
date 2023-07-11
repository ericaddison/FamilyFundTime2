package com.lutortech.familyfundtime.ui.family

import android.util.Log
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lutortech.familyfundtime.Constants.LOG_TAG
import com.lutortech.familyfundtime.model.family.Family
import com.lutortech.familyfundtime.model.family.FamilyOperations
import com.lutortech.familyfundtime.model.family.member.FamilyMemberOperations
import com.lutortech.familyfundtime.model.user.UserOperations
import com.lutortech.familyfundtime.ui.UiState
import com.lutortech.familyfundtime.ui.family.FamilyListEvent.UserEventClickedFamily
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FamilyListViewModel(
    private val uiState: UiState,
    userOperations: UserOperations,
    familyOperations: FamilyOperations,
    private val familyMemberOperations: FamilyMemberOperations
) : ViewModel() {

    // UI State
    val user = uiState.currentUser
    val isSignedIn: StateFlow<Boolean> = uiState.isSignedIn
    val selectedFamily = uiState.selectedFamily

    // Private flows
    private val selectedFamilyFlow = snapshotFlow { selectedFamily.value }

    init {
        viewModelScope.launch {
            // When the user changes, set SelectedFamily to null
            user.map { selectedFamily.value = null }.collect()
        }
    }

    private val familiesFromUserChangeFlow = user.map {
        it?.let { realUser -> familyOperations.getFamiliesForUser(realUser) } ?: setOf()
    }

    private val familiesFromSelectedFamilyFlow =
        selectedFamilyFlow.map { familyState ->
            val currentFamilies: Set<Family> = families.value
            familyState?.takeIf { it !in currentFamilies }?.let { currentFamilies + it }
                ?: currentFamilies
        }

    val families: StateFlow<Set<Family>> =
        merge(familiesFromUserChangeFlow, familiesFromSelectedFamilyFlow)
            .stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = setOf())

    // Event Handler
    fun onEvent(event: FamilyListEvent) {
        when (event) {
            is UserEventClickedFamily -> onSelectedFamilyChange(event)
        }
    }

    private fun onSelectedFamilyChange(event: UserEventClickedFamily) {
        if (selectedFamily.value == event.family) {
            return
        }

        selectedFamily.value = event.family
        viewModelScope.launch {
            Log.d(
                LOG_TAG,
                "Selected Family changed to [${event.family.url}], loading new family members"
            )
            val newFamilyMembers = familyMemberOperations.getFamilyMembersForFamily(event.family)
            uiState.currentFamilyMembers.value = newFamilyMembers
        }
    }

}