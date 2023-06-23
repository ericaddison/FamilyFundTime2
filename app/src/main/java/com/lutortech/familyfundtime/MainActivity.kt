package com.lutortech.familyfundtime

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lutortech.familyfundtime.Constants.LOG_TAG
import com.lutortech.familyfundtime.ui.theme.FamilyFundTimeTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.lutortech.familyfundtime.model.user.signin.FirebaseSignInOperations
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.lutortech.familyfundtime.model.familymember.FamilyMemberOperations
import com.lutortech.familyfundtime.model.familymember.FirebaseFamilyMemberOperations
import com.lutortech.familyfundtime.model.user.User
import com.lutortech.familyfundtime.model.user.signin.SignInOperations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var dataAccess: DataAccess
    private val signInOperations: SignInOperations<*> = FirebaseSignInOperations()
    private val familyMemberOperations: FamilyMemberOperations = FirebaseFamilyMemberOperations()

    private val user: MutableState<User?> = mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataAccess = FirebaseDataAccess()
        signInOperations.init(this) { onSignInResult(it) }
        setContent {
            FamilyFundTimeTheme {
                // A surface container using the 'background' color from the theme
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {

                    GrayButton(text = "Sign In") {
                        Log.d(LOG_TAG, "click!")
                        signInOperations.launchSignInActivity(this@MainActivity)
                    }

                    GrayButton(text = "Sign Out") {
                        Log.d(LOG_TAG, "click!")
                        signInOperations.signOut()
                        updateUser()
                    }

                    GrayButton(text = "Get Or Create FamilyMember for User") {
                        Log.d(LOG_TAG, "click!")

                        if(user.value == null) {
                            Log.i(LOG_TAG, "Cannot get family member when user is not signed in")
                        } else {
                            CoroutineScope(Dispatchers.IO).launch {
                                val familyMember = user.value?.let {
                                    familyMemberOperations.getOrCreateFamilyMember(it, "1234")
                                }
                                Log.i(LOG_TAG, "Found family member: $familyMember")
                            }
                        }
                    }

                    val rememberedUser by remember { user }
                    UserInfoCard(user = rememberedUser)
                }
            }
        }
        updateUser()
    }

    private fun updateUser() {
        user.value = signInOperations.currentUser()
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            updateUser()
            // ...
        } else {
            Log.w(LOG_TAG, "sign in failed: ${result.resultCode}")
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }
}

@Composable
fun UserInfoCard(user: User?) {
    Text("Email: ${user?.email()}", color = Color.Cyan)
    Text("Name: ${user?.displayName()}", color = Color.Cyan)
    Text("User Id: ${user?.id()}", color = Color.Cyan)
}

@Composable
fun GrayButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)) {
        Text(text, color = Color.White)
    }
}