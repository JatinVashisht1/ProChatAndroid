package com.example.demochatapplication.features.login.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.demochatapplication.core.CustomPaddingValues.MEDIUM
import com.example.demochatapplication.core.CustomPaddingValues.SMALL
import com.example.demochatapplication.features.login.ui.components.LoginScreenTextFieldComposable
import com.example.demochatapplication.features.login.ui.uiState.LoginScreenState

@Composable
fun LoginScreenParent(loginScreenViewModel: LoginScreenViewModel = hiltViewModel()) {
    val loginScreenState by remember{loginScreenViewModel.loginScreenState}

    LoginScreen(
        loginScreenState = loginScreenState,
        onUsernameTextFieldChanged = loginScreenViewModel::onUsernameTextFieldChange,
        onPasswordTextFieldChanged = loginScreenViewModel::onPasswordTextFieldChange,
        onLoginButtonClicked = loginScreenViewModel::onLoginButtonClicked,
    )

}

@Composable
fun LoginScreen(
    loginScreenState: LoginScreenState,
    onUsernameTextFieldChanged: (newUsernameString: String) -> Unit,
    onPasswordTextFieldChanged: (newPasswordString: String) -> Unit,
    onLoginButtonClicked: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(MEDIUM))

        LoginScreenTextFieldComposable(
            value = loginScreenState.usernameTextFieldState.text,
            onValueChange = onUsernameTextFieldChanged,
            modifier = Modifier
                .padding(horizontal = SMALL)
                .fillMaxWidth(),
            placeholder = { Text(text = loginScreenState.usernameTextFieldState.placeholder) },
            label = { Text(text = loginScreenState.usernameTextFieldState.label) },
        )

        Spacer(modifier = Modifier.height(MEDIUM))

        LoginScreenTextFieldComposable(
            value = loginScreenState.passwordTextFieldState.text,
            onValueChange = onPasswordTextFieldChanged,
            modifier = Modifier
                .padding(horizontal = SMALL)
                .fillMaxWidth(),
            placeholder = { Text(text = loginScreenState.passwordTextFieldState.placeholder) },
            label = { Text(text = loginScreenState.passwordTextFieldState.label) },
        )

        Spacer(modifier = Modifier.height(MEDIUM))

        Button(onClick = onLoginButtonClicked) {
            Text(text = "Login")
        }
    }
}