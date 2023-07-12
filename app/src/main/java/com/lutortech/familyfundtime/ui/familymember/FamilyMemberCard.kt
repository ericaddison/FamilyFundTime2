package com.lutortech.familyfundtime.ui.familymember

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.lutortech.familyfundtime.ui.ProfilePic
import java.text.NumberFormat
import java.util.Currency


@Composable
fun FamilyMemberCard(
    viewModel: FamilyMemberViewModel,
    modifier: Modifier = Modifier
) {

//    remembered states
    val totalBalance by viewModel.totalBalance.collectAsState()
    val mainBinBalance by viewModel.mainBalance.collectAsState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { },
        elevation = CardDefaults.elevatedCardElevation()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = modifier.fillMaxWidth(fraction = 0.2f),
                contentAlignment = Alignment.Center
            ) {
                ProfilePic(
                    picUrl = viewModel.profilePicUrl(),
                    modifier = modifier
                        .size(60.dp)
                )
            }
            Column(
                modifier = modifier
                    .padding(1.dp)
                    .fillMaxWidth(fraction = 0.7f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(text = viewModel.displayName() ?: "NO NAME")
                MoneyBinBalance(name = "total", balance = totalBalance)
                MoneyBinBalance(name = "main", balance = mainBinBalance)
            }
        }
    }

}

@Composable
fun MoneyBinBalance(name: String, balance: Double?, modifier: Modifier = Modifier) {
    val format: NumberFormat = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 0
    Text(text = "$name: ${balance?.let { format.format(it) } ?: "?"}")
}