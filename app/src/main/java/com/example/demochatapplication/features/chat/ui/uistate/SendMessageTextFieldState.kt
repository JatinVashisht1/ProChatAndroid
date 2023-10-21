package com.example.demochatapplication.features.chat.ui.uistate

data class SendMessageTextFieldState(
    val message: String = "",
    val placeholderValue: String = "Type...",
    val label: String = "Send",
)