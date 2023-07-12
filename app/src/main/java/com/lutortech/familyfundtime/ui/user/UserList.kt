package com.lutortech.familyfundtime.ui.user

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lutortech.familyfundtime.model.user.User


@Composable
fun UserList(viewModel: UserListViewModel, modifier: Modifier = Modifier) {

    // remembered state
    val allUsers by viewModel.allUsers.collectAsState()
    val currentUser by viewModel.user.collectAsState()

    Surface(
        modifier = modifier
            .fillMaxWidth()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {


            items(
                allUsers.toList().sortedWith(compareBy({ it.id != currentUser?.id }, { it.displayName }))
            ) {
                UserListItem(it, isSelf = it.id == currentUser?.id)
            }
        }
    }
}

@Composable
fun UserListItem(user: User, isSelf: Boolean, modifier: Modifier = Modifier) {
    val fontWeight = if (isSelf) FontWeight.Bold else FontWeight.Normal
    Text("${user.displayName}", fontWeight = fontWeight, modifier = modifier)
}