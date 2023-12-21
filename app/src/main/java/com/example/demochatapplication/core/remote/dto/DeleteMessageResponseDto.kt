package com.example.demochatapplication.core.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DeleteMessageResponseDto(
    val message: String,
    val success: Boolean
)