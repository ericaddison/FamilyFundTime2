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
import com.lutortech.familyfundtime.model.family.member.moneybin.FirebaseMoneyBinOperations
import com.lutortech.familyfundtime.model.family.member.moneybin.MoneyBinOperations
import com.lutortech.familyfundtime.model.family.transaction.FirebaseTransactionOperations
import com.lutortech.familyfundtime.model.family.transaction.TransactionOperations
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
    private val userOperations: UserOperations = FirebaseUserOperations()
    private val familyOperations: FamilyOperations = FirebaseFamilyOperations()
    private val moneyBinOperations: MoneyBinOperations = FirebaseMoneyBinOperations()
    private val transactionOperations: TransactionOperations = FirebaseTransactionOperations()

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
                            signInOperations.currentUser()
                                ?.also { user ->
                                    val newFamilyId = familyOperations.createFamily(user)
                                    Log.i(LOG_TAG, "New Family Id [$newFamilyId]")
                                } ?: "NOT SIGNED IN"
                        }
                    }

                    GrayButton(text = "Add User To Family") {
                        openDialog.value = true
                    }

                    GrayButton(text = "Get Families For User") {
                        CoroutineScope(Dispatchers.IO).launch {
                            user.value?.also { realUser ->
                                val families = familyOperations.getFamiliesForUser(realUser)
                                Log.i(
                                    LOG_TAG,
                                    "Found [${families.size}] families for user [${realUser.id}]: $families }"
                                )
                                userFamilies.value = families.map { it.id }.toSet()
                            }

                        }
                    }

                    GrayButton(text = "Get All Users") {
                        CoroutineScope(Dispatchers.IO).launch {
                            val allUsers = userOperations.getAllUsers()
                            Log.i(LOG_TAG, "all users: ${allUsers.map { it.displayName }}")
                        }
                    }

                    GrayButton(text = "Get moneybins for family member") {
                        CoroutineScope(Dispatchers.IO).launch {
                            // get a family member for the current user
                            user.value?.also { realUser ->
                                familyOperations.getFamiliesForUser(realUser).firstOrNull()
                                    ?.also { family ->
                                        val familyMember = familyOperations.getOrCreateFamilyMember(
                                            realUser,
                                            family
                                        )
                                        val moneyBins =
                                            moneyBinOperations.getMoneyBinsForFamilyMember(
                                                familyMember
                                            )
                                        Log.i(
                                            LOG_TAG,
                                            "Found moneybins for family member: $moneyBins"
                                        )
                                    } ?: Log.i(
                                    LOG_TAG,
                                    "No families found for user [${user.value?.id}"
                                )
                            } ?: Log.i(LOG_TAG, "User not logged in")


                        }
                    }

                    GrayButton(text = "Add $5 family member") {
                        CoroutineScope(Dispatchers.IO).launch {
                            // get a family member for the current user
                            user.value?.also { realUser ->
                                familyOperations.getFamiliesForUser(realUser).firstOrNull()
                                    ?.also { family ->
                                        val familyMember = familyOperations.getOrCreateFamilyMember(
                                            realUser,
                                            family
                                        )
                                        val moneyBins =
                                            moneyBinOperations.getMoneyBinsForFamilyMember(
                                                familyMember
                                            )
                                        transactionOperations.addMoney(
                                            5.0,
                                            moneyBins.first(),
                                            "TEST",
                                            "TITLE",
                                            "NOTE",
                                            familyMember
                                        )

                                    } ?: Log.i(
                                    LOG_TAG,
                                    "No families found for user [${user.value?.id}"
                                )
                            } ?: Log.i(LOG_TAG, "User not logged in")


                        }
                    }

                    UserInfoCard(user, userFamilies)
                }
            }
        }
        updateUser()
    }

    private fun updateUser() {
        user.value = signInOperations.currentUser()
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
//        val response = result.idpResponse
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
            Text("Email: ${myUser.value?.email}", color = Color.Cyan)
            Text("Name: ${myUser.value?.displayName}", color = Color.Cyan)
            Text("User Id: ${myUser.value?.id}", color = Color.Cyan)
            Text("Families: ${myFamilies.value}", color = Color.Cyan)
        }
    }
}

@Composable
fun UserFamiliesCard(families: Set<Family>) {
    families.forEach {
        Text("Family [${it.id}]")
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