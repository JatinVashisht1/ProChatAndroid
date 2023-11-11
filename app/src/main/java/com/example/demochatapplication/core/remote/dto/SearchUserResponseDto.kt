package com.example.demochatapplication.core.remote.dto

data class SearchUserResponseDto(
    val searchString: String,
    val users: List<String>
)