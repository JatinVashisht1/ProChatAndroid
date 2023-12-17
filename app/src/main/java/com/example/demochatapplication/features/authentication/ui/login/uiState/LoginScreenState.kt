package com.example.demochatapplication.features.authentication.ui.uistate

data class LoginScreenState(
    val usernameTextFieldState: LoginScreenTextFieldState = LoginScreenTextFieldState(
        placeholder = "enter username",
        label = "username",
        text = "def",
    ),
    val passwordTextFieldState: LoginScreenTextFieldState = LoginScreenTextFieldState(
        placeholder = "enter password",
        label = "password",
        text = "def",
    ),
)
