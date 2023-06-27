package com.lutortech.familyfundtime.model.family.member.moneybin

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.lutortech.familyfundtime.model.family.member.FamilyMember
import kotlinx.coroutines.tasks.await
import java.time.Instant

class FirebaseMoneyBinOperations : MoneyBinOperations {

    private val db = Firebase.firestore

    override suspend fun getMoneyBinsForFamilyMember(familyMember: FamilyMember): Set<MoneyBin> =
        db.collection("${familyMember.url}/${MoneyBin.COLLECTION}").get().await()
            .map { it.toMoneyBin() }.toSet()


    private fun QueryDocumentSnapshot.toMoneyBin() = MoneyBin(
        id = this.id,
        url = this.reference.path,
        createdTimestamp = Instant.ofEpochMilli(getLong(MoneyBin.FIELD_CREATED_TIMESTAMP) ?: 0),
        name = getString(MoneyBin.FIELD_NAME) ?: "NO NAME FOUND",
        note = getString(MoneyBin.FIELD_NOTE) ?: "NO NOTE FOUND",
        balance = getDouble(MoneyBin.FIELD_BALANCE) ?: Double.MIN_VALUE
    )
}


