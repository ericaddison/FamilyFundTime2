package com.lutortech.familyfundtime.ui.family

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.lutortech.familyfundtime.model.family.Family
import com.lutortech.familyfundtime.ui.family.FamilyListEvent.UserEventClickedFamily

@Composable
fun FamilyList(
    viewModel: FamilyListViewModel,
    modifier: Modifier = Modifier
) {

    // remembered state
    val families by viewModel.families.collectAsState()
    val selectedFamily by viewModel.selectedFamily.collectAsState()
    val showList by viewModel.showList.collectAsState()

    if (selectedFamily != null && !showList) {
        return
    }

    Surface(
        modifier = modifier
            .border(width = 1.dp, color = Color.Yellow)
            .padding(5.dp)
            .height(200.dp)
            .fillMaxWidth()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)

        ) {
            items(families.toList()) {
                FamilyListItem(family = it,
                    isSelected = it == selectedFamily,
                    modifier
                        .padding(10.dp)
                        .clickable {
                            viewModel.onEvent(
                                UserEventClickedFamily(it)
                            )
                        })
            }
        }
    }
}

@Composable
fun FamilyListItem(family: Family, isSelected: Boolean, modifier: Modifier = Modifier) {
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
    Text("Family: ${family.id}", fontWeight = fontWeight, modifier = modifier)
}