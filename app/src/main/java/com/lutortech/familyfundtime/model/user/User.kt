package com.lutortech.familyfundtime.model.user

data class User(val id: String, val displayName: String?, val email: String?) {
    companion object{
        const val COLLECTION = "users"
        const val FIELD_DISPLAY_NAME = "displayName"
        const val FIELD_EMAIL = "email"
    }
}