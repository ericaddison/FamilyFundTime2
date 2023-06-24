package com.lutortech.familyfundtime.model.user

interface UserOperations {

    suspend fun storeUser(user: User): Boolean

}