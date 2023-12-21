package com.example.demochatapplication.features.authentication.ui.login

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.demochatapplication.features.authentication.ui.login.components.LoginButtonComposable
import com.example.demochatapplication.features.authentication.ui.login.components.PasswordTextFieldComposable
import com.example.demochatapplication.features.authentication.ui.login.components.UsernameTextFieldComposable
import com.example.demochatapplication.features.authentication.ui.uistate.LoginScreenTextFieldState
import com.example.demochatapplication.features.authentication.ui.uistate.PasswordTextFieldProperties
import com.example.demochatapplication.features.authentication.ui.login.utils.PaddingValues
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow

@Composable
fun LoginScreenParent(
    loginScreenViewModel: LoginScreenViewModel = hiltViewModel(),
    navHostController: NavHostController,
) {
    val usernameTextFieldState = loginScreenViewModel.loginScreenState.value.usernameTextFieldState
    val context: Context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(key1 = Unit) {
        loginScreenViewModel.uiEvents.consumeAsFlow().collectLatest {
            when(it) {
                is UiEvents.NavigateTo -> {
                    if (it.shouldPopBackStack) {
                        navHostController.popBackStack()
                    }

                    navHostController.navigate(it.destination)
                }

                is UiEvents.ShowSnackbar -> {
                    scaffoldState.snackbarHostState.showSnackbar(it.message, actionLabel = "understood")
                }
            }
        }
    }


    Scaffold (scaffoldState = scaffoldState) {
    Box(
        modifier = Modifier
            .padding(it)
            .fillMaxSize()
    ) {
        LoginScreen(
            modifier = Modifier.fillMaxSize(),
            usernameTextFieldState = usernameTextFieldState,
            onUsernameValueChange = loginScreenViewModel::onUsernameTextFieldChange,
            onPasswordValueChange = loginScreenViewModel::onPasswordTextFieldChange,
            passwordTextFieldProperties = loginScreenViewModel.passwordTextFieldProperties.value,
            passwordTextFieldState = loginScreenViewModel.loginScreenState.value.passwordTextFieldState,
            onLoginButtonClicked = loginScreenViewModel::onLoginButtonClicked,
            onNavigateToSignUpScreenClicked = loginScreenViewModel::onNavigateToSignUpButtonClicked,
        )

        }
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
    onNavigateToSignUpScreenClicked: () -> Unit,
) {
    LazyColumn(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        item {
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

            Text(text = "or",)

            Spacer(modifier = Modifier.height(PaddingValues.MEDIUM))

            OutlinedButton(onClick = onNavigateToSignUpScreenClicked, modifier = Modifier
                .padding(horizontal = PaddingValues.LARGE)
                .fillMaxWidth()) {
                Text(text = "create account")
            }
        }
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