package com.example.demochatapplication.features.authentication.ui.uistate

data class LoginScreenTextFieldState(
    var text: String = "",
    val placeholder: String = "",
    val label: String = "",
)

data class PasswordTextFieldProperties(
    var showPassword: Boolean = false,
)