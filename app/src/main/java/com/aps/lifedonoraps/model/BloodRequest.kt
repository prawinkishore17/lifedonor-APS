package com.aps.lifedonoraps.model

import com.google.firebase.Timestamp

data class BloodRequest(
    val id: String = "",
    val patientName: String = "",
    val bloodGroup: String = "",
    val hospital: String = "",
    val contact: String = "",
    val urgency: String = "Normal",   // "Urgent" or "Normal"
    val status: String = "Open",      // "Open" or "Closed"
    val requesterId: String = "",
    val requesterName: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val createdAt: Timestamp? = null
)
