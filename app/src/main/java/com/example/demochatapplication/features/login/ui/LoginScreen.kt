package com.example.demochatapplication.features.login.ui

import android.app.Activity
import android.content.IntentSender
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.demochatapplication.core.CustomPaddingValues.MEDIUM
import com.example.demochatapplication.core.CustomPaddingValues.SMALL
import com.example.demochatapplication.features.login.ui.LoginScreenViewModel.Companion.REQUEST_CODE_GIS_SAVE_PASSWORD
import com.example.demochatapplication.features.login.ui.components.LoginScreenTextFieldComposable
import com.example.demochatapplication.features.login.ui.uiState.LoginScreenState
import com.google.android.gms.auth.api.identity.*
import timber.log.Timber


@Composable
fun LoginScreenParent(
    loginScreenViewModel: LoginScreenViewModel = hiltViewModel(),
) {
    val loginScreenState by remember { loginScreenViewModel.loginScreenState }
    val showHintPicker = remember { loginScreenViewModel.showHintPicker }
    val getPassword = remember { loginScreenViewModel.getPassword }
    val context = LocalContext.current

    LoginScreen(
        signInRequest = loginScreenViewModel.signInRequest,
        oneTapClient = loginScreenViewModel.signInClient,
        showHintPicker = showHintPicker.value,
        getPassword = getPassword.value,
        savePasswordRequest = loginScreenViewModel.getSavePasswordRequest(
            loginScreenState.usernameTextFieldState.text,
            loginScreenState.passwordTextFieldState.text
        ),
        activity = context as ComponentActivity,
        loginScreenState = loginScreenState,
        onUsernameTextFieldChanged = loginScreenViewModel::onUsernameTextFieldChange,
        onPasswordTextFieldChanged = loginScreenViewModel::onPasswordTextFieldChange,
        onLoginButtonClicked = loginScreenViewModel::onLoginButtonClicked,
        onGetPasswordButtonClicked = {
            loginScreenViewModel.getPassword.value = true
        },
        onSavePasswordRequestCompleted = {
            loginScreenViewModel.showHintPicker.value = false
        }
    ) {
        loginScreenViewModel.getPassword.value = false
    }

}

@Composable
fun LoginScreen(
    oneTapClient: SignInClient,
    getPassword: Boolean,
    showHintPicker: Boolean,
    activity: ComponentActivity,
    savePasswordRequest: SavePasswordRequest,
    loginScreenState: LoginScreenState,
    signInRequest: BeginSignInRequest,
    onUsernameTextFieldChanged: (newUsernameString: String) -> Unit,
    onPasswordTextFieldChanged: (newPasswordString: String) -> Unit,
    onGetPasswordButtonClicked: () -> Unit,
    onLoginButtonClicked: () -> Unit,
    onSavePasswordRequestCompleted: () -> Unit,
    onPasswordGettingCompleted: () -> Unit,
) {
    val launchActivityForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) {
        val requestCode = it.resultCode
        Timber.tag(HOME_SCREEN_TAG)
            .d("password is ${oneTapClient.getSignInCredentialFromIntent(it.data).password}")
    }

    val launchSavePasswordRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
    ) {result ->
        val requestCode = result.resultCode
        Timber.tag(HOME_SCREEN_TAG)
            .d("password is $requestCode ${Activity.RESULT_OK}"
//                    "${oneTapClient.getSignInCredentialFromIntent(result.data).password}"
            )
    }

    LaunchedEffect(key1 = showHintPicker, key2 = getPassword) {
        if (showHintPicker) {
            showSavePasswordPrompt(activity = activity, savePasswordRequest = savePasswordRequest, launchSavePasswordRequest = launchSavePasswordRequest)
            onSavePasswordRequestCompleted()
        }

        if (getPassword) {
            beginSignIn(
                oneTapClient = oneTapClient,
                signInRequest = signInRequest,
                activity = activity,
                launchActivityForResult = launchActivityForResult,
            )
            onPasswordGettingCompleted()
        }
    }


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

        Button(
            onClick = onLoginButtonClicked, modifier = Modifier
                .padding(horizontal = SMALL)
                .fillMaxWidth()
        ) {
            Text(text = "Login")
        }

        Button(
            onClick = onGetPasswordButtonClicked, modifier = Modifier
                .padding(horizontal = SMALL)
                .fillMaxWidth()
        ) {
            Text(text = "Get Password")
        }
    }
}

const val HOME_SCREEN_TAG = "loginscreentag"
const val REQ_ONE_TAP = 2

fun beginSignIn(
    oneTapClient: SignInClient,
    signInRequest: BeginSignInRequest,
    activity: ComponentActivity,
    launchActivityForResult: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
) {
    oneTapClient.beginSignIn(signInRequest)
        .addOnSuccessListener(activity) { result ->
            try {
                val intentSenderRequest =
                    IntentSenderRequest.Builder(result.pendingIntent)
                        .build()
                launchActivityForResult.launch(intentSenderRequest)


            } catch (e: IntentSender.SendIntentException) {
                Timber.tag(HOME_SCREEN_TAG)
                    .e("Couldn't start One Tap UI: %s", e.localizedMessage)
            }
        }
        .addOnFailureListener(activity) { e ->
            // No saved credentials found. Launch the One Tap sign-up flow, or
            // do nothing and continue presenting the signed-out UI.
            Timber.tag(HOME_SCREEN_TAG).d(e.localizedMessage)
        }
}

fun showSavePasswordPrompt(
    activity: ComponentActivity,
    savePasswordRequest: SavePasswordRequest,
    launchSavePasswordRequest: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
) {
    Identity.getCredentialSavingClient(activity)
        .savePassword(savePasswordRequest)
        .addOnSuccessListener { result ->
            Timber.tag(HOME_SCREEN_TAG).d("result is $result")

            val intentSenderRequest =
                IntentSenderRequest.Builder(result.pendingIntent)
                    .build()

            launchSavePasswordRequest.launch(intentSenderRequest);

//            startIntentSenderForResult(
//                activity,
//                result.pendingIntent.intentSender,
//                REQUEST_CODE_GIS_SAVE_PASSWORD,
//                /* fillInIntent = */
//                null,
//                /* flagsMask = */
//                0,
//                /* flagsValues = */
//                0,
//                /* extraFlags = */
//                0,
//                /* options = */
//                null,
//            )
        }
}