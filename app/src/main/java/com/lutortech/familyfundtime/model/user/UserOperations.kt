package com.lutortech.familyfundtime.model.user

import androidx.compose.runtime.MutableState

interface UserOperations {

    /** TODO: paginate this. */
    suspend fun getAllUsers(): Set<User>

    fun signOut()

    fun currentUser(): MutableState<User?>
    fun launchSignInActivity()
}