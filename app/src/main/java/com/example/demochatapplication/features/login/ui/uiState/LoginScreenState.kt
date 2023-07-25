package com.example.demochatapplication.features.login.ui.uiState

data class LoginScreenState(
    val usernameTextFieldState: LoginScreenTextFieldState = LoginScreenTextFieldState(
        placeholder = "enter username",
        label = "username"
    ),
    val passwordTextFieldState: LoginScreenTextFieldState = LoginScreenTextFieldState(
        placeholder = "enter password",
        label = "password"
    ),
)
