package com.example.demochatapplication.core.remote.dto

data class SignInBodyDto(
    val username: String,
    val password: String,
    val firebaseToken: String
)
