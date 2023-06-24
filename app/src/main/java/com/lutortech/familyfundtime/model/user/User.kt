package com.lutortech.familyfundtime.model.user

interface User {

    fun id(): String

    fun displayName(): String?

    fun email(): String?

    fun toMap() = mapOf(
        "id" to id(),
        "displayName" to displayName(),
        "email" to email()
    )
}