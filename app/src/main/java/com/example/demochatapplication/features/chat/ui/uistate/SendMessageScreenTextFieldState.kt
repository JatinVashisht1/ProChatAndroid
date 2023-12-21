package com.example.demochatapplication.features.chat.ui.uistate

data class SendMessageScreenTextFieldState(
    val message: String = "",
    val placeholderValue: String = "",
    val label: String = "",
)