package com.lutortech.familyfundtime.model.user

import android.net.Uri
import java.time.Instant

data class User(
    val id: String,
    val createdTimestamp: Instant,
    val displayName: String?,
    val email: String?,
    val profilePicUrl: Uri?,
    val lastSelectedFamilyId: String = ""
) {
    companion object {
        const val COLLECTION = "users"
        const val FIELD_CREATED_TIMESTAMP = "created_timestamp"
        const val FIELD_DISPLAY_NAME = "displayName"
        const val FIELD_EMAIL = "email"
        const val FIELD_PROFILE_PIC_URL = "profile_pic_url"
        const val FIELD_LAST_SELECTED_FAMILY_ID = "last_selected_family_id"

        fun dbDataMap(displayName: String?, email: String?, profilePicUrl: Uri?, lastSelectedFamily: String = "") = mapOf(
            FIELD_CREATED_TIMESTAMP to Instant.now().toEpochMilli(),
            FIELD_DISPLAY_NAME to displayName,
            FIELD_EMAIL to email,
            FIELD_PROFILE_PIC_URL to profilePicUrl.toString(),
            FIELD_LAST_SELECTED_FAMILY_ID to lastSelectedFamily
        )
    }
}