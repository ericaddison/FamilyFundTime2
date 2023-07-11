package com.lutortech.familyfundtime.ui.familymember

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lutortech.familyfundtime.ui.ProfilePic


@Composable
fun FamilyMemberCard(
    viewModel: FamilyMemberCardViewModel,
    modifier: Modifier = Modifier
) {

//     remembered states
//    val totalBalance by remember{ viewModel.totalBalance }
//    val mainBinBalance by remember{ viewModel.mainBinBalance }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { },
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProfilePic(
                picUrl = viewModel.profilePicUrl(),
                modifier = modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = 0.25f)
            )
            Column(
                modifier = modifier
                    .padding(1.dp)
                    .fillMaxWidth(fraction = 0.7f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = viewModel.displayName() ?: "NO NAME")
                Text(text = "Total $$: ???")
                Text(text = "Main Bin $$: ???")
            }
        }
    }

}