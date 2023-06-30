package com.lutortech.familyfundtime.model.invitation

import com.lutortech.familyfundtime.model.family.Family
import com.lutortech.familyfundtime.model.user.User
import java.time.Instant

enum class InvitationStatus {
    ACCEPTED,
    REJECTED,
    PENDING
}

data class Invitation(
    val id: String,
    val createdTimestamp: Instant,
    val from: User?,
    val to: User?,
    val family: Family?,
    val note: String?,
    val status: InvitationStatus
) {

    companion object {
        const val COLLECTION = "invitations"
        const val FIELD_FROM_USER_ID = "from_user_id"
        const val FIELD_TO_USER_ID = "to_user_id"
        const val FIELD_FAMILY_ID = "family_id"
        const val FIELD_NOTE = "note"
        const val FIELD_STATUS = "status"
        const val FIELD_CREATED_TIMESTAMP = "created_timestamp"

        fun dbDataMap(
            from: User,
            to: User,
            family: Family,
            note: String?,
            status: InvitationStatus
        ) =
            mapOf(
                FIELD_FROM_USER_ID to from.id,
                FIELD_TO_USER_ID to to.id,
                FIELD_FAMILY_ID to family.id,
                FIELD_NOTE to note,
                FIELD_STATUS to status.name,
                FIELD_CREATED_TIMESTAMP to Instant.now().toEpochMilli()
            )
    }

}