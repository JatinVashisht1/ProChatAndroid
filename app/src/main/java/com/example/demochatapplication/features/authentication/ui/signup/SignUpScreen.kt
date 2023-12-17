package com.example.demochatapplication.features.authentication.ui.signup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarData
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.demochatapplication.features.authentication.ui.login.utils.PaddingValues
import com.example.demochatapplication.features.authentication.ui.signup.components.SignUpScreenState
import com.example.demochatapplication.features.shared.composables.ErrorComposable
import com.example.demochatapplication.features.shared.composables.LoadingComposable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow

@Composable
fun SignUpScreenParent(
    signUpScreenViewModel: SignUpScreenViewModel = hiltViewModel(),
    navHostController: NavHostController,
) {
    val signUpScreenState by signUpScreenViewModel.signUpScreenState.collectAsStateWithLifecycle()
    val scaffoldState = rememberScaffoldState()
    val snackBarHostState = remember {scaffoldState.snackbarHostState}

    LaunchedEffect(key1 = Unit) {
        signUpScreenViewModel.signUpScreenUiEvents.consumeAsFlow().collectLatest {
            when (it) {
                is SignUpScreenUiEvents.NavigateTo -> {
                    navHostController.navigate(it.route)
                }
                is SignUpScreenUiEvents.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(it.message, actionLabel = "understood")
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
    ) {

        Box(modifier = Modifier
            .padding(it)
            .fillMaxSize()
        ) {
            when (val state = signUpScreenState) {
                is SignUpScreenState.Success -> {
                    val lazyListState = rememberLazyListState()
                    SignUpScreenContent(
                        signUpScreenState = state,
                        lazyListState = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                        onUsernameTextFieldChange = signUpScreenViewModel::onUsernameTextFieldValueChange,
                        onPasswordTextFieldChange = signUpScreenViewModel::onPasswordTextFieldValueChange,
                        onSignUpButtonClicked = signUpScreenViewModel::onSignUpButtonClicked
                    )
                }
            }
        }
    }
}

@Composable
fun SignUpScreenContent(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    signUpScreenState: SignUpScreenState.Success,
    onUsernameTextFieldChange: (String) -> Unit,
    onPasswordTextFieldChange: (String) -> Unit,
    onSignUpButtonClicked: () -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            OutlinedTextField(
                value = signUpScreenState.usernameTextFieldState.text,
                onValueChange = onUsernameTextFieldChange,
                placeholder = {
                    Text(text = signUpScreenState.usernameTextFieldState.placeholder)
                },
                label = {
                    Text(text = signUpScreenState.usernameTextFieldState.label)
                },
                modifier = Modifier
                    .padding(horizontal = PaddingValues.LARGE)
                    .fillMaxWidth(),
            )
            
            Spacer(modifier = Modifier.height(PaddingValues.MEDIUM))
            
            OutlinedTextField(
                value = signUpScreenState.passwordTextFieldState.text,
                onValueChange = onPasswordTextFieldChange,
                placeholder = {
                    Text(text = signUpScreenState.passwordTextFieldState.placeholder)
                },
                label = {
                    Text(text = signUpScreenState.passwordTextFieldState.label)
                },
                modifier = Modifier
                    .padding(horizontal = PaddingValues.LARGE)
                    .fillMaxWidth(),
            )
            
            Spacer(modifier = Modifier.height(PaddingValues.MEDIUM))
            
            Button(
                onClick = onSignUpButtonClicked,
                modifier = Modifier
                    .padding(horizontal = PaddingValues.LARGE)
                    .fillMaxWidth(),
            ) {
                Text(text = "Sign Up")
            }
        }
    }
}