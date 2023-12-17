package com.example.demochatapplication.features.authentication.ui.signup.uistate

sealed class SignUpScreenState {
    data class Success (
        val usernameTextFieldState: TextFieldState = TextFieldState(placeholder = "your username", label = "username"),
        val passwordTextFieldState: TextFieldState = TextFieldState(placeholder = "your password", label = "password")
    ): SignUpScreenState()
}

data class TextFieldState (
    val text: String = "",
    val placeholder: String = "",
    val label: String = "",
    val error: String = "",
)