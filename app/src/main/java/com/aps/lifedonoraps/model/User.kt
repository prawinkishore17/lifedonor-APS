package com.aps.lifedonoraps.model

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val bloodGroup: String = "",
    val userType: String = "Donor",   // "Donor" or "Recipient"
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val points: Long = 0,             // reward points
    val donationCount: Long = 0,
    val available: Boolean = true,    // donor availability toggle
    val fcmToken: String = ""
)
