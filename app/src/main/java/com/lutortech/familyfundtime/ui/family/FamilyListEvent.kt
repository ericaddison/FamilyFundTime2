package com.lutortech.familyfundtime.ui.family

import com.lutortech.familyfundtime.model.family.Family

sealed interface FamilyListEvent {

    data class UserEventClickedFamily(val family: Family): FamilyListEvent

}