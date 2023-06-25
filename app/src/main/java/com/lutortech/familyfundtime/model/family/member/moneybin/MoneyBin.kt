package com.lutortech.familyfundtime.model.family.member.moneybin

data class MoneyBin(
    val id: String,
    val name: String,
    val note: String,
    val balance: Double,
    val ownerId: String
) {
    companion object {
        const val COLLECTION = "moneybins"
        const val FIELD_BALANCE = "balance"
        const val FIELD_NAME = "name"
        const val FIELD_NOTE = "note"

        fun dbDataMap(name: String, note: String, balance: Double) =
            mapOf(
                FIELD_NAME to name,
                FIELD_NOTE to note,
                FIELD_BALANCE to balance,
            )

    }
}
