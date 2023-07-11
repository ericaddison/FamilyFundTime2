package com.lutortech.familyfundtime.model.user

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lutortech.familyfundtime.Constants.LOG_TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.Instant


class FirebaseUserOperations(
    private val sourceActivity: ComponentActivity
) : UserOperations {

    private val db = Firebase.firestore
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val signInLauncher: ActivityResultLauncher<Intent> =
        sourceActivity.registerForActivityResult(FirebaseAuthUIActivityResultContract())
        { res: FirebaseAuthUIAuthenticationResult ->
            onSignIn(res)
        }

    private val backgroundScope = CoroutineScope(Dispatchers.IO)

    private fun onSignIn(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            auth.currentUser?.also {
                backgroundScope.launch {
                    // see if we already have this user:
                    val newUser = getUserById(it.uid) ?: it.toUser().also { storeUser(it) }
                    _userStateFlow.value = newUser
                    Log.d(LOG_TAG, "Successful sign-in for user [${newUser.id}]")
                }
            }
        } else {
            _userStateFlow.value = null
            Log.w(LOG_TAG, "Sign-in failed: [${result.resultCode}")
        }
    }


    private suspend fun storeUser(user: User) {
        val document = db.document("${User.COLLECTION}/${user.id}").get().await()

        // user already exists, return early
        if (document.exists()) {
            Log.d(LOG_TAG, "User [${user.id}] already exists, not storing")
            return
        }

        // user does not exist in DB, store it
        db.collection(User.COLLECTION).document(user.id)
            .set(User.dbDataMap(user.displayName, user.email, user.profilePicUrl)).await()
        Log.d(LOG_TAG, "Stored user [${user.id}]")
    }

    override suspend fun getAllUsers(): Set<User> =
        db.collection(User.COLLECTION).get().await().map { it.toUser() }.toSet()

    private suspend fun getUserById(userId: String): User? =
        db.document("${User.COLLECTION}/$userId").get().await().takeIf { it.exists() }?.toUser()


    override fun launchSignInActivity() {
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
        _userStateFlow.value = null
        Log.d(LOG_TAG, "user signed out")
    }

    private val _userStateFlow = MutableStateFlow(auth.currentUser?.toUser())
    override fun currentUser(): StateFlow<User?> = _userStateFlow.asStateFlow()

    private fun FirebaseUser.toUser() = User(uid, Instant.EPOCH, displayName, email, photoUrl)

    companion object {
        fun DocumentSnapshot.toUser(): User {
            val displayName = this.getString(User.FIELD_DISPLAY_NAME)
                ?: throw IllegalStateException("Field FIELD_DISPLAY_NAME not found for user [${reference.path}]")
            val email = this.getString(User.FIELD_EMAIL)
                ?: throw IllegalStateException("Field FIELD_EMAIL not found for user [${reference.path}]")
            val profilePicUrl = this.getString(User.FIELD_PROFILE_PIC_URL)
                ?: throw IllegalStateException("Field FIELD_PROFILE_PIC_URL not found for user [${reference.path}]")
            val createdTimestamp = this.getLong(User.FIELD_CREATED_TIMESTAMP)
                ?: throw IllegalStateException("Field CREATED_TIMESTAMP not found for user [${reference.path}]")
            return User(
                this.id,
                Instant.ofEpochMilli(createdTimestamp),
                displayName,
                email,
                Uri.parse(profilePicUrl)
            )
        }
    }

}
