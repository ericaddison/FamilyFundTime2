package com.lutortech.familyfundtime.ui.user

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lutortech.familyfundtime.model.user.User
import com.lutortech.familyfundtime.model.user.UserOperations
import com.lutortech.familyfundtime.ui.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserListViewModel(uiState: UiState, userOperations: UserOperations): ViewModel() {

    // private flows
    private val _allUsers: MutableStateFlow<Set<User>> = MutableStateFlow(setOf())

    // UI State
    val user: StateFlow<User?> = uiState.currentUser
    val allUsers: StateFlow<Set<User>> = _allUsers

    init {
        viewModelScope.launch {
            _allUsers.value = userOperations.getAllUsers()
        }
    }
}