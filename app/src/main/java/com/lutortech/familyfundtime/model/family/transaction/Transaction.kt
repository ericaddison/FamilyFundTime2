package com.lutortech.familyfundtime.model.family.transaction

data class Transaction(
    // The amount of the transaction
    val amount: Double,

    // Where the money came from.
    // This can be the id of a MoneyBin for transfers,
    // "ALLOWANCE" for allowance payments,
    // "GIFT" for gifts
    val from: String,

    // This should always be the id of the MoneyBin this transaction was sent to
    val to: String,

    // The title of this transaction
    val title: String,

    // A user supplied note about this transaction
    val note: String
)