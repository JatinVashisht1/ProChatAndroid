package com.example.demochatapplication.features.authentication.domain.model

data class SignUpResponseModel(
    val success: Boolean,
    val jwtToken: String,
)
