package com.lutortech.familyfundtime.ui.user

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lutortech.familyfundtime.model.user.User
import com.lutortech.familyfundtime.model.user.UserOperations
import kotlinx.coroutines.launch

class UserListViewModel(userOperations: UserOperations): ViewModel() {

    // UI State
    val user = userOperations.currentUser()
    val allUsers: MutableState<Set<User>> = mutableStateOf(setOf())

    init {
        viewModelScope.launch {
            allUsers.value = userOperations.getAllUsers()
        }
    }
}