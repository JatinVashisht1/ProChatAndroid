package com.example.demochatapplication.features.login.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.demochatapplication.features.login.ui.components.PasswordTextFieldComposable
import com.example.demochatapplication.features.login.ui.components.UsernameTextFieldComposable
import com.example.demochatapplication.features.login.ui.uiState.LoginScreenTextFieldState
import com.example.demochatapplication.features.login.ui.uiState.PasswordTextFieldProperties
import com.example.demochatapplication.features.login.ui.utils.PaddingValues

@Composable
fun LoginScreenParent(loginScreenViewModel: LoginScreenViewModel = hiltViewModel()) {
    val usernameTextFieldState = loginScreenViewModel.loginScreenState.value.usernameTextFieldState
    Box(modifier = Modifier.fillMaxSize()) {
        LoginScreen(
            modifier = Modifier.fillMaxSize(),
            usernameTextFieldState = usernameTextFieldState,
            onUsernameValueChange = loginScreenViewModel::onUsernameTextFieldChange,
            onPasswordValueChange =  loginScreenViewModel::onPasswordTextFieldChange,
            passwordTextFieldProperties = loginScreenViewModel.passwordTextFieldProperties.value,
            passwordTextFieldState = loginScreenViewModel.loginScreenState.value.passwordTextFieldState

        )
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    usernameTextFieldState: LoginScreenTextFieldState,
    onUsernameValueChange: (String) -> Unit,
    onPasswordValueChange: (String) -> Unit,
    passwordTextFieldProperties: PasswordTextFieldProperties,
    passwordTextFieldState: LoginScreenTextFieldState,
) {
    Column (modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        UsernameTextFieldComposable(
            value = usernameTextFieldState.text,
            placeholder = { Text(text = usernameTextFieldState.placeholder) },
            label = { Text(text = usernameTextFieldState.label) },
            onValueChange = onUsernameValueChange,
            modifier = Modifier.padding(horizontal = PaddingValues.LARGE),
        )

        Spacer(modifier = Modifier.height(PaddingValues.MEDIUM))

        PasswordTextFieldComposable(
            placeholder = { Text(passwordTextFieldState.placeholder) },
            label = { Text(passwordTextFieldState.label) },
            onPasswordValueChange = onPasswordValueChange,
            showPassword = passwordTextFieldProperties.showPassword,
            password = passwordTextFieldState.text,
            modifier = Modifier.padding(horizontal = PaddingValues.LARGE),
        )
    }
}