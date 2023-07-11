package com.lutortech.familyfundtime.model.family.member.allowance

import com.firebase.ui.auth.data.model.User
import java.time.Instant
import kotlin.time.DurationUnit

data class Allowance(
    val id: String,
    val url: String,
    val createdTimestamp: Instant,
    val amount: Double,
    val payee: User,
    val lastApplyTime: Instant,
    val frequency: Int,
    val timeUnit: DurationUnit
)