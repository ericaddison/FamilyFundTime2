package com.lutortech.familyfundtime.model.family.transaction

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lutortech.familyfundtime.Constants.LOG_TAG
import com.lutortech.familyfundtime.model.family.member.FamilyMember
import com.lutortech.familyfundtime.model.family.member.moneybin.MoneyBin
import kotlinx.coroutines.tasks.await

typealias FirebaseTransaction = com.google.firebase.firestore.Transaction

/**
 * The ids in this class are expected to be full Firebase paths that point to a given object
 */
class FirebaseTransactionOperations : TransactionOperations {

    private val db = Firebase.firestore

    override suspend fun addMoney(
        amount: Double,
        moneyBin: MoneyBin,
        source: String,
        title: String,
        note: String?,
        familyMember: FamilyMember
    ) {
        val familyPath = familyMember.getFamilyPath()
        val moneyBinRef = db.document(moneyBin.url)
        val newTransactionRef =
            db.collection("$familyPath/${Transaction.COLLECTION}").document()
        db.runTransaction { transaction ->
            Log.d(LOG_TAG, "Enter transaction: addMoney")

            addMoneyInternal(transaction, moneyBinRef, amount)

            val transactionData =
                Transaction.dbDataMap(
                    amount,
                    source = source,
                    destinationMoneyBinId = moneyBin.id,
                    title = title,
                    note = note,
                    familyMemberId = familyMember.id
                )
            transaction.set(newTransactionRef, transactionData)


            Log.d(LOG_TAG, "End transaction: addMoney")
        }.await()
    }

    private fun addMoneyInternal(
        transaction: FirebaseTransaction,
        moneyBinRef: DocumentReference,
        amount: Double
    ) {
        val moneyBinDocument = transaction.get(moneyBinRef)
        val balance = moneyBinDocument.getDouble(MoneyBin.FIELD_BALANCE)
            ?: throw IllegalArgumentException("Unable to fetch balance for moneybin [${moneyBinRef.path}]")
        transaction.update(moneyBinRef, MoneyBin.FIELD_BALANCE, balance + amount)
        Log.i(LOG_TAG, "Added $[$amount] to moneyBin [${moneyBinRef.path}]")
    }

    private fun FamilyMember.getFamilyPath(): String {
        return url.split("/").take(2).joinToString("/")
    }

    override suspend fun removeMoney(
        amount: Double,
        moneyBin: MoneyBin,
        source: String,
        title: String,
        note: String?,
        familyMember: FamilyMember
    ) {
        val familyPath = familyMember.getFamilyPath()
        val moneyBinRef = db.document(moneyBin.url)
        val newTransactionRef =
            db.collection("$familyPath/${Transaction.COLLECTION}").document()
        db.runTransaction { transaction ->
            Log.d(LOG_TAG, "Enter transaction: removeMoney")

            removeMoneyInternal(transaction, moneyBinRef, amount)

            val transactionData =
                Transaction.dbDataMap(
                    amount,
                    source = source,
                    destinationMoneyBinId = moneyBin.id,
                    title = title,
                    note = note,
                    familyMemberId = familyMember.id
                )
            transaction.set(newTransactionRef, transactionData)

            Log.d(LOG_TAG, "End transaction: removeMoney")
        }.await()
    }

    private fun removeMoneyInternal(
        transaction: FirebaseTransaction,
        moneyBinRef: DocumentReference,
        amount: Double
    ) {
        val moneyBinDocument = transaction.get(moneyBinRef)
        val balance = moneyBinDocument.getDouble(MoneyBin.FIELD_BALANCE)
            ?: throw IllegalArgumentException("Unable to fetch balance for moneybin [${moneyBinRef.path}]")
        if (balance < amount) {
            throw InsufficientFundsException(balance, amount)
        }
        transaction.update(moneyBinRef, MoneyBin.FIELD_BALANCE, balance - amount)
        Log.i(LOG_TAG, "Removed $[$amount] from moneyBin [${moneyBinRef.path}]")
    }

    override suspend fun transferMoney(
        amount: Double,
        source: String,
        sourceMoneyBin: MoneyBin?,
        destinationMoneyBin: MoneyBin,
        title: String,
        note: String?,
        familyMember: FamilyMember
    ) {
        // If the source of this transfer is from another family member, require that the
        // sourceMoneyBinId is non-null
        if (sourceMoneyBin == null && source == Transaction.SOURCE_FAMILY_MEMBER) {
            throw IllegalArgumentException(
                "Require non-null sourceMoneyBin when source is [$source]"
            )
        }

        if (amount < 0) {
            throw IllegalArgumentException("Transfer amount must be positive, found [$amount]")
        }

        val destinationMoneyBinRef = db.document(destinationMoneyBin.url)
        val sourceMoneyBinRef = sourceMoneyBin?.let {
            db.document(sourceMoneyBin.url)
        }
        val familyId = familyMember.getFamilyPath()
        val newTransactionRef =
            db.collection("$familyId/${Transaction.COLLECTION}").document()

        db.runTransaction { transaction ->

            Log.d(LOG_TAG, "Enter transaction: transferMoney")

            val sourceSnapshot = sourceMoneyBinRef?.let { transaction.get(it) }
            val destinationSnapshot = transaction.get(destinationMoneyBinRef)

            if (sourceSnapshot?.exists() == false) {
                throw IllegalArgumentException("Source moneybin does not exist: id [${sourceMoneyBin.url}]")
            }

            if (!destinationSnapshot.exists()) {
                throw IllegalArgumentException("Destination moneybin does not exist: id [${destinationMoneyBin.url}]")
            }

            sourceSnapshot?.also { removeMoneyInternal(transaction, it.reference, amount) }

            addMoneyInternal(transaction, destinationMoneyBinRef, amount)

            val transactionData =
                Transaction.dbDataMap(
                    amount,
                    source = source,
                    sourceMoneyBinId = sourceMoneyBin?.id,
                    destinationMoneyBinId = destinationMoneyBin.id,
                    title = title,
                    note = note,
                    familyMemberId = familyMember.id
                )
            transaction.set(newTransactionRef, transactionData)

            Log.d(LOG_TAG, "End transaction: transferMoney")
        }.await()
    }
}