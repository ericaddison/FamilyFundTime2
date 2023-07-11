package com.lutortech.familyfundtime.model.family.transaction

import com.lutortech.familyfundtime.model.family.member.FamilyMember
import com.lutortech.familyfundtime.model.family.member.moneybin.MoneyBin

interface TransactionOperations {

    suspend fun addMoney(
        amount: Double,
        moneyBin: MoneyBin,
        source: String,
        title: String,
        note: String? = null,
        familyMember: FamilyMember
    )

    suspend fun removeMoney(
        amount: Double,
        moneyBin: MoneyBin,
        source: String,
        title: String,
        note: String? = null,
        familyMember: FamilyMember
    )

    // Amount must be positive
    suspend fun transferMoney(
        amount: Double,
        source: String,
        sourceMoneyBin: MoneyBin?,
        destinationMoneyBin: MoneyBin,
        title: String,
        note: String?,
        familyMember: FamilyMember
    )
}

data class InsufficientFundsException(val balance: Double, val transactionAmount: Double) :
    Exception("Insufficient funds available for transaction: [$transactionAmount] > [$balance]")