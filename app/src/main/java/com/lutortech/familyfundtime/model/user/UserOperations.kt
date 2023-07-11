package com.lutortech.familyfundtime.model.user

import kotlinx.coroutines.flow.StateFlow

interface UserOperations {

    /** TODO: paginate this. */
    suspend fun getAllUsers(): Set<User>

    fun signOut()

    fun currentUser(): StateFlow<User?>
    fun launchSignInActivity()
}