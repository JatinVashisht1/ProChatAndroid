package com.example.demochatapplication.features.login.ui.components
//
//import android.app.Activity
//import androidx.activity.compose.ManagedActivityResultLauncher
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.ActivityResult
//import androidx.activity.result.IntentSenderRequest
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.runtime.Composable
//import com.example.demochatapplication.features.login.ui.LOGIN_SCREEN_TAG
//import com.google.android.gms.auth.api.identity.SignInClient
//import com.google.android.gms.common.api.ApiException
//import com.google.android.gms.common.api.CommonStatusCodes
//import timber.log.Timber
//
///**
// * A composable function that creates a managed activity result launcher to get saved passwords from Google One Tap.
// *
// * @param oneTapClient The Google SignInClient for one-tap sign-in functionality.
// * @param onPasswordGettingCompleted Callback for handling the completion of password retrieval.
// * @param onUsernameTextFieldChanged Callback for handling changes to the username text field.
// * @param onPasswordTextFieldChanged Callback for handling changes to the password text field.
// *
// * @return The managed activity result launcher for the save password request.
// */
//@Composable
//fun getLaunchActivityResultToGetPassword(
//    oneTapClient: SignInClient,
//    onPasswordGettingCompleted: () -> Unit,
//    onUsernameTextFieldChanged: (newUsernameString: String) -> Unit,
//    onPasswordTextFieldChanged: (newPasswordString: String) -> Unit,
//): ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult> {
//    val launchSavePasswordRequest = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartIntentSenderForResult(),
//    ) { result ->
//        try {
////        Timber.tag(LOGIN_SCREEN_TAG).d("Result in getLaunchActivityResult is $result")
//          if(result.resultCode == Activity.RESULT_OK) {
//
//          }
//
//        } catch (e: ApiException) {
//            when (e.statusCode) {
//                CommonStatusCodes.CANCELED -> {
//                    Timber.tag(LOGIN_SCREEN_TAG).d("One-tap dialog was closed.")
//                    // Don't re-prompt the user.
//                    onPasswordGettingCompleted()
//                }
//
//                CommonStatusCodes.NETWORK_ERROR -> {
//                    Timber.tag(LOGIN_SCREEN_TAG).d("One-tap encountered a network error.")
//                    onPasswordGettingCompleted()
//                }
//
//                else -> {
//                    Timber.tag(LOGIN_SCREEN_TAG)
//                        .d("Unable to get credentials else branch\n$e\n${e.message}\n${e.localizedMessage}")
//                }
//            }
//        } catch (e: Exception) {
//            Timber.tag(LOGIN_SCREEN_TAG)
//                .d("Unable to show get credentials catch exception parent\n$e")
//        }
//    }
//
//    return launchSavePasswordRequest
//}
