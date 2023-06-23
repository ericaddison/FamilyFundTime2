package com.lutortech.familyfundtime.model.family

import com.lutortech.familyfundtime.model.user.User

interface Family {

    fun id(): String

    fun familyMembers(): Set<User>


}