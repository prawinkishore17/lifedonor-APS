package com.aps.lifedonoraps.model

import com.google.firebase.Timestamp

data class Donation(
    val id: String = "",
    val donorId: String = "",
    val donorName: String = "",
    val requestId: String = "",
    val hospital: String = "",
    val pointsAwarded: Long = 0,
    val verified: Boolean = false,
    val donatedAt: Timestamp? = null
)
