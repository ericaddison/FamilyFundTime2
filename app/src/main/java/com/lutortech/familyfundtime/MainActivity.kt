package com.lutortech.familyfundtime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lutortech.familyfundtime.model.family.FamilyOperations
import com.lutortech.familyfundtime.model.family.FirebaseFamilyOperations
import com.lutortech.familyfundtime.model.family.member.FamilyMemberOperations
import com.lutortech.familyfundtime.model.family.member.FirebaseFamilyMemberOperations
import com.lutortech.familyfundtime.model.family.member.moneybin.FirebaseMoneyBinOperations
import com.lutortech.familyfundtime.model.family.member.moneybin.MoneyBinOperations
import com.lutortech.familyfundtime.model.family.transaction.FirebaseTransactionOperations
import com.lutortech.familyfundtime.model.family.transaction.TransactionOperations
import com.lutortech.familyfundtime.model.user.FirebaseUserOperations
import com.lutortech.familyfundtime.model.user.UserOperations
import com.lutortech.familyfundtime.ui.UiState
import com.lutortech.familyfundtime.ui.family.FamilyList
import com.lutortech.familyfundtime.ui.family.FamilyListViewModel
import com.lutortech.familyfundtime.ui.familymember.FamilyMemberList
import com.lutortech.familyfundtime.ui.familymember.FamilyMemberListViewModel
import com.lutortech.familyfundtime.ui.signin.SignIn
import com.lutortech.familyfundtime.ui.signin.SignInViewModel
import com.lutortech.familyfundtime.ui.theme.FamilyFundTimeTheme
import com.lutortech.familyfundtime.ui.user.UserList
import com.lutortech.familyfundtime.ui.user.UserListViewModel
import kotlinx.coroutines.flow.StateFlow


class MainActivity : ComponentActivity() {

    private val userOperations: UserOperations = FirebaseUserOperations(this)
    private val familyOperations: FamilyOperations = FirebaseFamilyOperations()
    private val familyMemberOperations: FamilyMemberOperations = FirebaseFamilyMemberOperations()
    private val moneyBinOperations: MoneyBinOperations = FirebaseMoneyBinOperations()
    private val transactionOperations: TransactionOperations = FirebaseTransactionOperations()
    private val uiState = UiState(userOperations, familyOperations, familyMemberOperations)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FamilyFundTimeTheme {
                // A surface container using the 'background' color from the theme
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(5.dp)
                ) {

                    val standardModifier = Modifier
//                        .border(width = 1.dp, color = Color.Cyan)
                        .padding(1.dp)

                    SignIn(
                        SignInViewModel(
                            uiState,
                            userOperations = userOperations
                        ),
                        modifier = standardModifier.height(45.dp)
                    )

//                    GrayButton(text = "Create New Family") {
//                        CoroutineScope(Dispatchers.IO).launch {
//                            Log.d(LOG_TAG, "creating family")
//                            uiState.currentUser.value?.also {
//                                val newFamily = familyOperations.createFamily(
//                                    it
//                                )
//                                uiState.setSelectedFamily(newFamily)
//                            }
//                        }
//                    }

                    val isSignedIn by uiState.isSignedIn.collectAsState()
                    if (isSignedIn) {
                        signedInUi(standardModifier)
                    }
                    UserList(
                        viewModel = UserListViewModel(uiState, userOperations),
                        modifier = standardModifier
                    )
                }
            }
        }
    }

    @Composable
    private fun signedInUi(modifier: Modifier = Modifier) {
        GrayBorderToggleButton(
            text = "All Families",
            toggleState = uiState.showFamilyList
        ) {
            uiState.setShowFamilyList(!uiState.showFamilyList.value)
        }

        FamilyList(
            FamilyListViewModel(
                uiState,
                userOperations,
                familyOperations,
                familyMemberOperations
            ),
            modifier = modifier.fillMaxHeight(fraction = 0.5f)
        )
        FamilyMemberList(
            FamilyMemberListViewModel(
                uiState,
                moneyBinOperations
            ),
            modifier = modifier
                .fillMaxHeight(fraction = 0.75f)
        )
    }

}

@Composable
fun GrayBorderToggleButton(
    text: String,
    toggleState: StateFlow<Boolean>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val toggle by toggleState.collectAsState()
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
        border = BorderStroke(
            width = 4.dp,
            if (toggle) Color.White else Color.DarkGray
        ),
        modifier = modifier
    ) {
        Text(text, color = Color.White)
    }
}