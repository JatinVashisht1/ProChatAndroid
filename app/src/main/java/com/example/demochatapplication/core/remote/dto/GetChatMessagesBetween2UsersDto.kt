package com.example.demochatapplication.core.remote.dto

data class GetChatMessagesBetween2UsersDto(
    val messages: List<Message> = emptyList(),
    val username1: String = "",
    val username2: String = "",
)