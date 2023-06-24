package com.lutortech.familyfundtime.model.signin

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.lutortech.familyfundtime.Constants.LOG_TAG
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.lutortech.familyfundtime.model.user.FirebaseUser
import com.lutortech.familyfundtime.model.user.User

/**
 * Details of using FirebaseAuth from
 * https://firebaseopensource.com/projects/firebase/firebaseui-android/auth/readme/
 */
class FirebaseSignInOperations : SignInOperations<FirebaseAuthUIAuthenticationResult> {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    init {
        auth.currentUser?.also {
            Log.i(LOG_TAG, "User already signed in: ${auth.currentUser?.displayName}")
        }
    }

    override fun init(sourceActivity: ComponentActivity, onSignInCallback: (FirebaseAuthUIAuthenticationResult) -> Unit) {
        signInLauncher =
            sourceActivity.registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res: FirebaseAuthUIAuthenticationResult ->
                onSignInCallback.invoke(res)
            }
    }

    override fun launchSignInActivity(
        sourceActivity: ComponentActivity
    ) {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().setRequireName(true).build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        // Sign out first so full user picker flow is shown instead of autologin.
        AuthUI.getInstance().signOut(sourceActivity)

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun currentUser(): User? = auth.currentUser?.let { FirebaseUser(it) }

}