package com.lutortech.familyfundtime.model.family.transaction

import java.time.Instant
import java.util.UUID

data class Transaction(
    val id: String,

    val url: String,

    val createdTimestamp: Instant,

    // The amount of the transaction
    val amount: Double,

    // Where the money came from.
    // This "MONEY_BIN" for a money bin,
    // "ALLOWANCE" for allowance payments,
    // "GIFT" for gifts
    val source: String,

    // The id of the moneybin this transaction came from, or null if automated
    val sourceMoneyBinId: String?,

    // This should always be the id of the MoneyBin this transaction was sent to
    val destinationMoneyBinId: String,

    // The title of this transaction
    val title: String,

    // A user supplied note about this transaction
    val note: String?,

    // The Id of the FamilyMember who initiated this transaction, or null if automated
    val familyMemberId: String?
) {

    companion object {
        const val COLLECTION = "transactions"
        const val FIELD_AMOUNT = "amount"
        const val FIELD_SOURCE = "source"
        const val FIELD_SOURCE_MONEY_BIN_ID = "source_money_bin_id"
        const val FIELD_DESTINATION_MONEY_BIN_ID = "destination_money_bin_id"
        const val FIELD_TITLE = "title"
        const val FIELD_NOTE = "note"
        const val FIELD_FAMILY_MEMBER_ID = "family_member_id"
        const val FIELD_CREATED_TIMESTAMP = "created_timestamp"
        const val SOURCE_FAMILY_MEMBER = "family_member"

        fun dbDataMap(
            amount: Double,
            source: String,
            sourceMoneyBinId: String? = null,
            destinationMoneyBinId: String,
            title: String,
            note: String?,
            familyMemberId: String?
        ) =
            mapOf(
                FIELD_AMOUNT to amount,
                FIELD_SOURCE to source,
                FIELD_SOURCE_MONEY_BIN_ID to sourceMoneyBinId,
                FIELD_DESTINATION_MONEY_BIN_ID to destinationMoneyBinId,
                FIELD_TITLE to title,
                FIELD_NOTE to note,
                FIELD_FAMILY_MEMBER_ID to familyMemberId,
                FIELD_CREATED_TIMESTAMP to Instant.now().toEpochMilli()
            )

    }
}