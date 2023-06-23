package com.lutortech.familyfundtime.model.user

interface User {

    fun id(): String

    fun displayName(): String?

    fun email(): String?
}