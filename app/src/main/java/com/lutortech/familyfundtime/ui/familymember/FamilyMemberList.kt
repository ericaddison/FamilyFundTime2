package com.lutortech.familyfundtime.ui.familymember

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FamilyMemberList(
    viewModel: FamilyMemberListViewModel,
    modifier: Modifier = Modifier
) {

// remembered state
    val familyMembers by viewModel.familyMembers.collectAsState()

    Surface(
        modifier = modifier
            .fillMaxWidth()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(familyMembers.toList()) {
                FamilyMemberCard(
                    viewModel = viewModel.familyMemberViewModel(it),
                    modifier
                )
            }
        }
    }
}