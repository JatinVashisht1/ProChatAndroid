package com.example.demochatapplication.features.accounts.data.dto

data class SearchUserResponseDto(
    val searchString: String,
    val users: List<String>
)