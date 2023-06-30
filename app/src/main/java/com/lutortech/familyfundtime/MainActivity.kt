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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lutortech.familyfundtime.Constants.LOG_TAG
import com.lutortech.familyfundtime.model.family.Family
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
import com.lutortech.familyfundtime.ui.family.FamilyList
import com.lutortech.familyfundtime.ui.family.FamilyListViewModel
import com.lutortech.familyfundtime.ui.familymember.FamilyMemberList
import com.lutortech.familyfundtime.ui.familymember.FamilyMemberListViewModel
import com.lutortech.familyfundtime.ui.signin.SignIn
import com.lutortech.familyfundtime.ui.signin.SignInViewModel
import com.lutortech.familyfundtime.ui.theme.FamilyFundTimeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private val userOperations: UserOperations = FirebaseUserOperations(this)
    private val familyOperations: FamilyOperations = FirebaseFamilyOperations()
    private val familyMemberOperations: FamilyMemberOperations = FirebaseFamilyMemberOperations()
    private val moneyBinOperations: MoneyBinOperations = FirebaseMoneyBinOperations()
    private val transactionOperations: TransactionOperations = FirebaseTransactionOperations()

    // UI state
    private val userFamilies: SnapshotStateList<Family> = mutableStateListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FamilyFundTimeTheme {
                // A surface container using the 'background' color from the theme
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {

                    SignIn(
                        SignInViewModel(
                            userOperations = userOperations
                        )
                    )

                    GrayButton(text = "Create New Family") {
                        CoroutineScope(Dispatchers.IO).launch {
                            Log.d(LOG_TAG, "creating family")
                            userOperations.currentUser().value?.also {
                                familyOperations.createFamily(
                                    it
                                )
                            }
                        }
                    }

                    FamilyList(FamilyListViewModel(userOperations, familyOperations, familyMemberOperations))
                    FamilyMemberList(FamilyMemberListViewModel(familyMemberOperations, familyOperations, moneyBinOperations))
                }
            }
        }
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

@Composable
fun SignInScreen() {


}