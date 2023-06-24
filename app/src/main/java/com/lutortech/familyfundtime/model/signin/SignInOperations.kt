package com.lutortech.familyfundtime.model.signin

import androidx.activity.ComponentActivity
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.lutortech.familyfundtime.model.user.User

interface SignInOperations<T> {

    fun signOut()

    fun currentUser(): User?

    fun init(
        sourceActivity: ComponentActivity,
        onSignInCallback: (FirebaseAuthUIAuthenticationResult) -> Unit
    )

    fun launchSignInActivity(sourceActivity: ComponentActivity)
}