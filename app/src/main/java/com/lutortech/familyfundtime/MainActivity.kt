package com.lutortech.familyfundtime

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.lutortech.familyfundtime.Constants.LOG_TAG
import com.lutortech.familyfundtime.model.family.Family
import com.lutortech.familyfundtime.model.family.FamilyOperations
import com.lutortech.familyfundtime.model.family.FirebaseFamilyOperations
import com.lutortech.familyfundtime.model.familymember.FamilyMemberOperations
import com.lutortech.familyfundtime.model.familymember.FirebaseFamilyMemberOperations
import com.lutortech.familyfundtime.model.signin.FirebaseSignInOperations
import com.lutortech.familyfundtime.model.signin.SignInOperations
import com.lutortech.familyfundtime.model.user.FirebaseUserOperations
import com.lutortech.familyfundtime.model.user.User
import com.lutortech.familyfundtime.model.user.UserOperations
import com.lutortech.familyfundtime.ui.theme.FamilyFundTimeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private val signInOperations: SignInOperations<*> = FirebaseSignInOperations()
    private val familyMemberOperations: FamilyMemberOperations = FirebaseFamilyMemberOperations()
    private val userOperations: UserOperations = FirebaseUserOperations()
    private val familyOperations: FamilyOperations = FirebaseFamilyOperations()

    private val user: MutableState<User?> = mutableStateOf(null)
    private val userFamilies: MutableState<Set<String>> = mutableStateOf(setOf())
    val openDialog = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

                    GrayButton(text = "Find all FamilyMembers for User") {
                        Log.d(LOG_TAG, "click!")

                        if (user.value == null) {
                            Log.i(LOG_TAG, "Cannot get family member when user is not signed in")
                        } else {
                            CoroutineScope(Dispatchers.IO).launch {
                                val familyMembers = user.value?.let {
                                    familyMemberOperations.getFamilyMembers(it.id())
                                }?.map {}

                                Log.i(LOG_TAG, "Found family members: $familyMembers")

                            }
                        }
                    }

                    GrayButton(text = "Store User") {
                        Log.d(LOG_TAG, "click!")

                        if (user.value == null) {
                            Log.i(LOG_TAG, "Cannot get store user when user is not signed in")
                        } else {
                            CoroutineScope(Dispatchers.IO).launch {
                                user.value?.also {

                                    val storedUser = userOperations.storeUser(it)
                                    Log.i(LOG_TAG, "Stored user? [$storedUser]")
                                }

                            }
                        }
                    }

                    GrayButton(text = "Create Family") {
                        CoroutineScope(Dispatchers.IO).launch {
                            val newFamilyId = signInOperations.currentUser()
                                ?.let { familyOperations.createFamily(it) } ?: "NOT SIGNED IN"
                            Log.i(LOG_TAG, "Family Id [$newFamilyId]")
                        }
                    }

                    GrayButton(text = "Add User To Family") {
                        openDialog.value = true
                    }

                    GrayButton(text = "Get Families For User") {
                        CoroutineScope(Dispatchers.IO).launch {
                            user.value?.also {
                                val familyIds = familyOperations.getFamiliesForUser(it.id())
                                Log.i(
                                    LOG_TAG,
                                    "Found [${familyIds.size}] families for user [${it.id()}]: $familyIds }"
                                )
                                userFamilies.value = familyIds
                            }

                        }
                    }


                    UserInfoCard(user, userFamilies)
                    AddUserToFamilyDialog(
                        openDialog,
                        user,
                        familyMemberOperations,
                        familyOperations
                    )
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
fun UserInfoCard(user: MutableState<User?>, userFamilies: MutableState<Set<String>>) {
    val myUser = remember { user }
    val myFamilies = remember { userFamilies }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable { },
        elevation = CardDefaults.outlinedCardElevation()
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text("Email: ${myUser.value?.email()}", color = Color.Cyan)
            Text("Name: ${myUser.value?.displayName()}", color = Color.Cyan)
            Text("User Id: ${myUser.value?.id()}", color = Color.Cyan)
            Text("Families: ${myFamilies.value}", color = Color.Cyan)
        }
    }
}

@Composable
fun UserFamiliesCard(families: Set<Family>) {
    families.forEach {
        Text("Family [${it.id()}]")
    }
}

@Composable
fun GrayButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
    ) {
        Text(text, color = Color.White)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserToFamilyDialog(
    openDialog: MutableState<Boolean>,
    user: MutableState<User?>,
    familyMemberOperations: FamilyMemberOperations,
    familyOperations: FamilyOperations
) {
    val myUser = remember { user }
    val myOpenDialog = remember { openDialog }
    val textState = remember { mutableStateOf(TextFieldValue("")) }

    if (myOpenDialog.value) {
        Dialog(onDismissRequest = { myOpenDialog.value = false }) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Enter Family Id to add user to")
                    TextField(
                        value = textState.value,
                        onValueChange = { textState.value = it },
                        textStyle = TextStyle(color = Color.Cyan),
                        placeholder = { Text("Enter Family Id") }
                    )
                    GrayButton(text = "Add User") {
                        myUser.value?.also { realUser ->
                            CoroutineScope(Dispatchers.IO).launch {

                                // Check that family exists
                                val familyId = "M3Yl3xilunkucM4h4Buf" //textState.value.text
                                if (familyOperations.familyExists(familyId)) {
                                    familyMemberOperations.getOrCreateFamilyMember(
                                        realUser,
                                        familyId
                                    )
                                } else {
                                    Log.i(LOG_TAG, "No family found with Id [$familyId]")
                                }
                            }
                        } ?: Log.i(LOG_TAG, "Cannot add user to familyt: User is NULL")
                        myOpenDialog.value = false
                    }

                }
            }
        }
    }
}