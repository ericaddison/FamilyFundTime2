package com.lutortech.familyfundtime.ui.familymember

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lutortech.familyfundtime.model.family.member.FamilyMember
import com.lutortech.familyfundtime.model.family.member.moneybin.MoneyBin
import com.lutortech.familyfundtime.model.family.member.moneybin.MoneyBinOperations
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FamilyMemberViewModel(
    private val familyMember: StateFlow<FamilyMember>,
    private val moneyBinOperations: MoneyBinOperations
) : ViewModel() {

    // UI State
    val allMoneyBins: StateFlow<Set<MoneyBin>> = familyMember.map {
        moneyBinOperations.getMoneyBinsForFamilyMember(it)
    }.stateIn(viewModelScope, SharingStarted.Lazily, initialValue = setOf())

    val mainMoneyBin: StateFlow<MoneyBin?> =
        allMoneyBins.map { bins -> bins.firstOrNull { it.name == MoneyBin.MONEYBIN_NAME_DEFAULT } }
            .stateIn(viewModelScope, SharingStarted.Lazily, initialValue = null)

    val totalBalance: StateFlow<Double?> =
        allMoneyBins.map { bins -> bins.takeIf { it.isNotEmpty() }?.sumOf { it.balance } }
            .stateIn(viewModelScope, SharingStarted.Lazily, initialValue = null)

    val mainBalance: StateFlow<Double?> = mainMoneyBin.map { it?.balance }
        .stateIn(viewModelScope, SharingStarted.Lazily, initialValue = null)

    fun profilePicUrl(): Uri? = familyMember.value.user.profilePicUrl

    fun displayName(): String? = familyMember.value.user.displayName
}