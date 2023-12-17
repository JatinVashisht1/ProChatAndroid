package com.example.demochatapplication.features.authentication.domain.model

data class SignUpBodyModel(
    val username: String,
    val password: String,
    val firebaseRegistrationToken: String
)
