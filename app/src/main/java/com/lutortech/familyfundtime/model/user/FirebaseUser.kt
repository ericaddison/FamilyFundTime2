package com.lutortech.familyfundtime.model.user

class FirebaseUser(private val underlyingUser: com.google.firebase.auth.FirebaseUser) : User {

    override fun id(): String = underlyingUser.uid

    override fun displayName(): String? = underlyingUser.displayName

    override fun email(): String? = underlyingUser.email
}

