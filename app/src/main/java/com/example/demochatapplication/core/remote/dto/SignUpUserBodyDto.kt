package com.example.demochatapplication.core.remote.dto

data class SignUpUserBodyDto(
    val username: String,
    val password: String,
    val firebaseToken: String,
)
