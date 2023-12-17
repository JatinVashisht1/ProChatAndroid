package com.example.demochatapplication.core.remote.dto

data class SignUpUserResponseDto(
    val message: String,
    val success: Boolean,
    val token: String
)