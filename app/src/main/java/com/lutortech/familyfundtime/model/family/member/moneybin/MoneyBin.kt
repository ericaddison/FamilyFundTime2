package com.lutortech.familyfundtime.model.family.member.moneybin

import java.time.Instant

data class MoneyBin(
    val id: String,
    val url: String,
    val createdTimestamp: Instant,
    val name: String,
    val note: String,
    val balance: Double) {
    companion object {
        const val COLLECTION = "moneybins"
        const val FIELD_BALANCE = "balance"
        const val FIELD_NAME = "name"
        const val FIELD_NOTE = "note"
        const val FIELD_CREATED_TIMESTAMP = "created_timestamp"

        fun dbDataMap(name: String, note: String, balance: Double) =
            mapOf(
                FIELD_NAME to name,
                FIELD_NOTE to note,
                FIELD_BALANCE to balance,
                FIELD_CREATED_TIMESTAMP to Instant.now().toEpochMilli()
            )

    }
}
