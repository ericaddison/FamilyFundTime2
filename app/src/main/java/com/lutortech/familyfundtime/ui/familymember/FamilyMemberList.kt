package com.lutortech.familyfundtime.ui.familymember

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lutortech.familyfundtime.model.family.member.FamilyMember
import com.lutortech.familyfundtime.ui.ProfilePic

@Composable
fun FamilyMemberList(viewModel: FamilyMemberListViewModel, modifier: Modifier = Modifier) {

// remembered state
    val familyMembers by viewModel.familyMembers.collectAsState(initial = setOf())

    Surface(
        modifier = modifier
            .border(width = 1.dp, color = Color.Cyan)
            .padding(5.dp)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(familyMembers.toList()) {
                FamilyMemberCard(
                    familyMember = it,
                    modifier
                        .padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun FamilyMemberCard(familyMember: FamilyMember, modifier: Modifier = Modifier) {

//     remembered states
//    val totalBalance by remember{ viewModel.totalBalance }
//    val mainBinBalance by remember{ viewModel.mainBinBalance }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(15.dp)
            .clickable { },
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Row() {
            Text(text = familyMember.user.displayName ?: "NO NAME")
            ProfilePic(picUrl = familyMember.user.profilePicUrl)
            Column() {
                Text(text = "Total $$: ???")
                Text(text = "Main Bin $$: ???")
            }
        }
    }

}