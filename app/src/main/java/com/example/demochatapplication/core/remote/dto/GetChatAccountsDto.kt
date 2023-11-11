package com.example.demochatapplication.core.remote.dto

data class GetChatAccountsDto(
    val accounts: List<Account>?,
    val user: String?,
)