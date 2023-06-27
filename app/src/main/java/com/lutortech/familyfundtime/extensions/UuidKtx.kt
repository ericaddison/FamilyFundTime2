package com.lutortech.familyfundtime.extensions

import android.util.Base64
import android.util.Base64.URL_SAFE
import java.nio.ByteBuffer
import java.util.UUID

fun UUID.toBase64(): String =
    ByteBuffer.wrap(byteArrayOf(16)).let {
        it.putLong(mostSignificantBits)
        it.putLong(leastSignificantBits)
        Base64.encodeToString(it.array(), URL_SAFE)
    }

