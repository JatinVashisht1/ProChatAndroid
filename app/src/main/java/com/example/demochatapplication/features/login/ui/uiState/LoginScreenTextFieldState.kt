package com.example.demochatapplication.features.login.ui.uiState

data class LoginScreenTextFieldState(
    var text: String = "",
    val placeholder: String = "",
    val label: String = "",
)

data class PasswordTextFieldProperties(
    var showPassword: Boolean = false,
)