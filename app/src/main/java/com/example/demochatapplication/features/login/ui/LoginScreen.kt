
/**
 * This file contains the UI components and logic for the login screen of the chat application.
 */
package com.example.demochatapplication.features.login.ui

import android.app.Application
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.demochatapplication.core.CustomPaddingValues.MEDIUM
import com.example.demochatapplication.core.CustomPaddingValues.SMALL
import com.example.demochatapplication.features.login.ui.components.LoginScreenTextFieldComposable
import com.example.demochatapplication.features.login.ui.components.beginSignIn
import com.example.demochatapplication.features.login.ui.components.getLaunchActivityResultToGetPassword
import com.example.demochatapplication.features.login.ui.components.showSavePasswordPrompt
import com.example.demochatapplication.features.login.ui.uiState.LoginScreenState
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SavePasswordRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import timber.log.Timber

/**
 * Composable function that represents the parent composable for the login screen.
 * It displays the main login screen UI and handles interactions with the UI components.
 *
 * @param loginScreenViewModel The view model for the login screen, obtained via Hilt ViewModel injection.
 */
@Composable
fun LoginScreenParent(
    loginScreenViewModel: LoginScreenViewModel = hiltViewModel(),
    context: Context,
) {
    // Retrieve the current state and variables from the ViewModel using 'rememberSaveable'.
    val loginScreenState by remember { loginScreenViewModel.loginScreenState }
    val showHintPicker = remember { loginScreenViewModel.showSavePasswordOneTapUi }
    val getPassword = remember { loginScreenViewModel.getPassword }
    val oneTapClient = remember { loginScreenViewModel.signInClient }

    // Create a managed activity result launcher for the save password request.
    val launchSavePasswordRequest = getLaunchActivityResultToGetPassword(
        oneTapClient = oneTapClient,
        onPasswordGettingCompleted = loginScreenViewModel::onPasswordGettingCompleted,
        onUsernameTextFieldChanged = loginScreenViewModel::onUsernameTextFieldChange,
        onPasswordTextFieldChanged = loginScreenViewModel::onPasswordTextFieldChange
    )

    // Call the main 'LoginScreen' composable with the necessary parameters.
    LoginScreen(
        oneTapClient = oneTapClient,
        getPassword = getPassword.value,
        showHintPicker = showHintPicker.value,
        activity = context as ComponentActivity,
        savePasswordRequest = loginScreenViewModel.getSavePasswordRequest(
            loginScreenState.usernameTextFieldState.text,
            loginScreenState.passwordTextFieldState.text
        ),
        loginScreenState = loginScreenState,
        signInRequest = loginScreenViewModel.signInRequest,
        onUsernameTextFieldChanged = loginScreenViewModel::onUsernameTextFieldChange,
        onPasswordTextFieldChanged = loginScreenViewModel::onPasswordTextFieldChange,
        onLoginButtonClicked = loginScreenViewModel::onLoginButtonClicked,
        onSavePasswordRequestCompleted = loginScreenViewModel::onSavePasswordRequestComplete,
        onPasswordGettingCompleted = loginScreenViewModel::onPasswordGettingCompleted,
        launchSavePasswordRequest = launchSavePasswordRequest,
    )
}

/**
 * Composable function that represents the login screen UI and handles interactions with its components.
 *
 * @param launchSavePasswordRequest The managed activity result launcher for the save password request.
 * @param oneTapClient The Google SignInClient for one-tap sign-in functionality.
 * @param getPassword Flag indicating whether to get the password from the user's device.
 * @param showHintPicker Flag indicating whether to show the save password hint picker.
 * @param activity The current ComponentActivity.
 * @param savePasswordRequest The save password request for the current login attempt.
 * @param loginScreenState The state of the login screen UI.
 * @param signInRequest The sign-in request for one-tap sign-in.
 * @param onUsernameTextFieldChanged Callback for handling changes to the username text field.
 * @param onPasswordTextFieldChanged Callback for handling changes to the password text field.
 * @param onLoginButtonClicked Callback for handling the login button click event.
 * @param onSavePasswordRequestCompleted Callback for handling the completion of the save password request.
 * @param onPasswordGettingCompleted Callback for handling the completion of password retrieval.
 */
@Composable
fun LoginScreen(
    launchSavePasswordRequest: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
    oneTapClient: SignInClient,
    getPassword: Boolean,
    showHintPicker: Boolean,
    activity: ComponentActivity,
    savePasswordRequest: SavePasswordRequest,
    loginScreenState: LoginScreenState,
    signInRequest: BeginSignInRequest,
    onUsernameTextFieldChanged: (newUsernameString: String) -> Unit,
    onPasswordTextFieldChanged: (newPasswordString: String) -> Unit,
    onLoginButtonClicked: () -> Unit,
    onSavePasswordRequestCompleted: () -> Unit,
    onPasswordGettingCompleted: () -> Unit,
) {
    // Create an activity result launcher for handling the one-tap sign-in flow.
    val launchActivityForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) {result ->
        try {
            val credentials = oneTapClient.getSignInCredentialFromIntent(result.data);
            val username = credentials.id
            val password = credentials.password

            Timber.tag(LOGIN_SCREEN_TAG).d(/* message = */"Username: $username, Password: $password")
        } catch (e: ApiException) {
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    Timber.tag(LOGIN_SCREEN_TAG).d("One-tap dialog was closed.")
                    // Don't re-prompt the user.
                    onPasswordGettingCompleted()
                }

                CommonStatusCodes.NETWORK_ERROR -> {
                    Timber.tag(LOGIN_SCREEN_TAG).d("One-tap encountered a network error.")
                    onPasswordGettingCompleted()
                }

                else -> {
                    Timber.tag(LOGIN_SCREEN_TAG)
                        .d("Unable to get credentials else branch\n$e\n${e.message}\n${e.localizedMessage}")
                }
            }
        } catch (e: Exception) {
            Timber.tag(LOGIN_SCREEN_TAG)
                .d("Unable to show get credentials catch exception parent\n$e")
        }
    }

    // Execute side effects when 'showHintPicker' or 'getPassword' flags change.
    LaunchedEffect(key1 = showHintPicker, key2 = getPassword) {
        if (showHintPicker) {
            // Show the save password prompt and handle the request completion.
            showSavePasswordPrompt(
                activity = activity,
                savePasswordRequest = savePasswordRequest,
                launchSavePasswordRequest = launchSavePasswordRequest
            )
            onSavePasswordRequestCompleted()
        }

        if (getPassword) {
            // Begin one-tap sign-in and handle the password retrieval completion.
            beginSignIn(
                oneTapClient = oneTapClient,
                signInRequest = signInRequest,
                activity = activity,
                launchActivityForResult = launchActivityForResult,
            )
            onPasswordGettingCompleted()
        }
    }

    // Compose the UI for the login screen.
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

        // Login button
        Button(
            onClick = onLoginButtonClicked,
            modifier = Modifier
                .padding(horizontal = SMALL)
                .fillMaxWidth()
        ) {
            Text(text = "Login")
        }

        /*
        // Uncomment this button if needed
        Button(
            onClick = onGetPasswordButtonClicked,
            modifier = Modifier
                .padding(horizontal = SMALL)
                .fillMaxWidth()
        ) {
            Text(text = "Get Password")
        }
        */
    }
}

// Constant tag used for Timber logging.
const val LOGIN_SCREEN_TAG = "loginscreentag"
