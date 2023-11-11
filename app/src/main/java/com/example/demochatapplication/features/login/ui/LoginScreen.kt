package com.example.demochatapplication.features.login.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.demochatapplication.features.login.ui.components.LoginButtonComposable
import com.example.demochatapplication.features.login.ui.components.PasswordTextFieldComposable
import com.example.demochatapplication.features.login.ui.components.UsernameTextFieldComposable
import com.example.demochatapplication.features.login.ui.uistate.LoginScreenTextFieldState
import com.example.demochatapplication.features.login.ui.uistate.PasswordTextFieldProperties
import com.example.demochatapplication.features.login.ui.utils.PaddingValues
import com.example.demochatapplication.features.shared.usersettings.UserSettings
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow

@Composable
fun LoginScreenParent(
    loginScreenViewModel: LoginScreenViewModel = hiltViewModel(),
    navHostController: NavHostController,
) {
    val context: Context = LocalContext.current
    LaunchedEffect(key1 = Unit) {
        loginScreenViewModel.uiEvents.consumeAsFlow().collectLatest {
            when(it) {
                is UiEvents.NavigateTo -> {
                    navHostController.popBackStack()
                    navHostController.navigate(it.destination)
                }
            }
        }
    }

    val usernameTextFieldState = loginScreenViewModel.loginScreenState.value.usernameTextFieldState
    Box(modifier = Modifier.fillMaxSize()) {
        LoginScreen(
            modifier = Modifier.fillMaxSize(),
            usernameTextFieldState = usernameTextFieldState,
            onUsernameValueChange = loginScreenViewModel::onUsernameTextFieldChange,
            onPasswordValueChange = loginScreenViewModel::onPasswordTextFieldChange,
            passwordTextFieldProperties = loginScreenViewModel.passwordTextFieldProperties.value,
            passwordTextFieldState = loginScreenViewModel.loginScreenState.value.passwordTextFieldState,
            onLoginButtonClicked = loginScreenViewModel::onLoginButtonClicked,
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
    onLoginButtonClicked: () -> Unit,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        UsernameTextFieldComposable(
            value = usernameTextFieldState.text,
            placeholder = { Text(text = usernameTextFieldState.placeholder) },
            label = { Text(text = usernameTextFieldState.label) },
            onValueChange = onUsernameValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingValues.LARGE),
        )

        Spacer(modifier = Modifier.height(PaddingValues.MEDIUM))

        PasswordTextFieldComposable(
            placeholder = { Text(passwordTextFieldState.placeholder) },
            label = { Text(passwordTextFieldState.label) },
            onPasswordValueChange = onPasswordValueChange,
            showPassword = passwordTextFieldProperties.showPassword,
            password = passwordTextFieldState.text,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingValues.LARGE),
        )

        Spacer(modifier = Modifier.height(PaddingValues.MEDIUM))

        LoginButtonComposable(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PaddingValues.LARGE),
            onLoginButtonClicked = onLoginButtonClicked,
        )

        Spacer(modifier = Modifier.height(PaddingValues.MEDIUM))
    }
}

//@Composable
//@Preview
//fun PreviewLoginScreen() {
//    LoginScreen(
//        usernameTextFieldState = LoginScreenTextFieldState(),
//        onUsernameValueChange = {},
//        onPasswordValueChange = {},
//        passwordTextFieldProperties = PasswordTextFieldProperties(),
//        passwordTextFieldState = LoginScreenTextFieldState(),
//    ) {
//
//    }
//}