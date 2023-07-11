package com.lutortech.familyfundtime.ui.signin

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.lutortech.familyfundtime.model.user.User
import com.lutortech.familyfundtime.model.user.UserOperations
import com.lutortech.familyfundtime.ui.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class SignInViewModel(
    private val uiState: UiState,
    private val userOperations: UserOperations
): ViewModel() {

    // UI State
    val user: StateFlow<User?> = uiState.currentUser
    val isSignedIn: Flow<Boolean> = uiState.isSignedIn

    // Event Handler
    fun onEvent(event: SignInEvent){
        when(event) {
            SignInEvent.USER_EVENT_LAUNCH_SIGN_IN -> userOperations.launchSignInActivity()
            SignInEvent.USER_EVENT_SIGN_OUT -> userOperations.signOut()
        }
    }

}