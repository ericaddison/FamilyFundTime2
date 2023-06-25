package com.lutortech.familyfundtime.model.family

import com.lutortech.familyfundtime.model.family.member.FamilyMember
import java.time.Instant

data class Family(val id: String, val familyMembers: Set<FamilyMember>, val createdTimestamp: Instant) {

    companion object {
        const val COLLECTION = "families"
        const val FIELD_CREATOR_USER_ID = "creatorUserId"
        const val FIELD_CREATED_TIMESTAMP = "created_timestamp"

        fun dbDataMap(creatorId: String) =
            mapOf(
                FIELD_CREATOR_USER_ID to creatorId,
                FIELD_CREATED_TIMESTAMP to Instant.now().toEpochMilli()
            )
    }
}