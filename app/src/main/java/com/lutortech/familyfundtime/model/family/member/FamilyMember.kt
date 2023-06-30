package com.lutortech.familyfundtime.model.family.member

import com.lutortech.familyfundtime.model.user.User
import java.time.Instant

data class FamilyMember(
    val id: String,
    val url: String,
    val createdTimestamp: Instant,
    val user: User,
    val isOwner: Boolean,
    val isAdmin: Boolean
) {

    companion object {
        const val COLLECTION = "members"
        const val FIELD_USER_ID = "user_id"
        const val FIELD_IS_ADMIN = "is_admin"
        const val FIELD_IS_OWNER = "is_owner"
        const val FIELD_CREATED_TIMESTAMP = "created_timestamp"

        fun dbDataMap(userId: String, isOwner: Boolean, isAdmin: Boolean, createdTimestamp: Long = Instant.now().toEpochMilli()) =
            mapOf(
                FIELD_USER_ID to userId,
                FIELD_IS_OWNER to isOwner,
                FIELD_IS_ADMIN to isAdmin,
                FIELD_CREATED_TIMESTAMP to createdTimestamp
            )
    }

}

