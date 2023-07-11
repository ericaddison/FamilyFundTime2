package com.lutortech.familyfundtime.ui.familymember

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lutortech.familyfundtime.ui.UiState

@Composable
fun FamilyMemberList(
    viewModel: FamilyMemberListViewModel,
    modifier: Modifier = Modifier
) {

// remembered state
    val familyMembers by remember { viewModel.familyMembers }
    val isSignedIn by viewModel.isSignedIn.collectAsState(initial = false)

    if (!isSignedIn) {
        return
    }

    Surface(
        modifier = modifier
            .border(width = 1.dp, color = Color.Cyan)
            .padding(5.dp)
            .height(200.dp)
            .fillMaxWidth()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(familyMembers.toList()) {
                FamilyMemberCard(
                    viewModel = viewModel.familyMemberCardViewModel(it),
                    modifier
                )
            }
        }
    }
}