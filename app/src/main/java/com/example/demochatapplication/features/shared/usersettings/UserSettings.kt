package com.example.demochatapplication.features.shared.usersettings

import kotlinx.serialization.Serializable

@Serializable()
data class UserSettings(
    val username: String = "",
    val password: String = "",
    val token: String = "",
    val firebaseRegistrationToken: String = "",
)
