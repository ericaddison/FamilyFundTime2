package com.lutortech.familyfundtime.model.family.member

import java.time.Instant

data class FamilyMember(
    val id: String,
    val userId: String,
    val isOwner: Boolean,
    val isAdmin: Boolean,
    val createdTimestamp: Instant
) {

    companion object {
        const val COLLECTION = "members"
        const val FIELD_USER_ID = "userId"
        const val FIELD_IS_ADMIN = "isAdmin"
        const val FIELD_IS_OWNER = "isOwner"
        const val FIELD_CREATED_TIMESTAMP = "created_timestamp"

        fun dbDataMap(userId: String, isOwner: Boolean, isAdmin: Boolean) =
            mapOf(
                FIELD_USER_ID to userId,
                FIELD_IS_OWNER to isOwner,
                FIELD_IS_ADMIN to isAdmin,
                FIELD_CREATED_TIMESTAMP to Instant.now().toEpochMilli()
            )

    }

}

