package com.lutortech.familyfundtime

import android.util.Base64
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.nio.ByteBuffer
import java.util.UUID

private const val FAMILY_FUND_TIME = "FamilyFundTime"

class FirebaseDataAccess : DataAccess
{

    val db = FirebaseFirestore.getInstance()

    override fun createUser() {
        // Create a new user with a first and last name
        val user = hashMapOf(
            "first" to "Ada",
            "last" to "Lovelace",
            "born" to 1815,
            "id" to uuidToBase64(UUID.randomUUID())
        )

        Log.i(FAMILY_FUND_TIME, "About to add user $user")

        // Add a new document with a generated ID
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(FAMILY_FUND_TIME, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(FAMILY_FUND_TIME, "Error adding document", e)
            }
    }

    override fun logUsers() {
        db.collection("users").get().addOnSuccessListener { result ->
            for (document in result) {
                Log.d(FAMILY_FUND_TIME, "${document.id} => ${document.data}")
            }
        }
    }

    private fun uuidToBase64(uuid: UUID): String {
        val bytes = ByteBuffer.allocate(16)
        bytes.putLong(uuid.mostSignificantBits)
        bytes.putLong(uuid.mostSignificantBits)
        return Base64.encodeToString(bytes.array(), Base64.URL_SAFE)
    }

}