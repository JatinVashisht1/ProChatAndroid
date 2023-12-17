package com.example.demochatapplication.features.authentication.domain.model

data class SignInBodyEntity(
    val username: String,
    val password: String,
    val firebaseRegistrationToken: String
)
