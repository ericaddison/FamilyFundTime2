package com.lutortech.familyfundtime.ui.signin

import androidx.compose.runtime.MutableState
import com.lutortech.familyfundtime.model.user.User
import com.lutortech.familyfundtime.model.user.UserOperations

class SignInViewModel(
    private val userOperations: UserOperations
) {

    // UI State
    val user: MutableState<User?> = userOperations.currentUser()

    // Event Handler
    fun onEvent(event: SignInEvent){
        when(event) {
            SignInEvent.USER_EVENT_LAUNCH_SIGN_IN -> userOperations.launchSignInActivity()
            SignInEvent.USER_EVENT_SIGN_OUT -> userOperations.signOut()
        }
    }

}