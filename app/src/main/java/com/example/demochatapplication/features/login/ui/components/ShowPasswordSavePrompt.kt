package com.example.demochatapplication.features.login.ui.components

//import androidx.activity.ComponentActivity
//import androidx.activity.compose.ManagedActivityResultLauncher
//import androidx.activity.result.ActivityResult
//import androidx.activity.result.IntentSenderRequest
//import androidx.activity.result.contract.ActivityResultContracts
//import com.example.demochatapplication.features.login.ui.LOGIN_SCREEN_TAG
//import com.google.android.gms.auth.api.identity.Identity
//import com.google.android.gms.auth.api.identity.SavePasswordRequest
//import timber.log.Timber
//
///**
// * Shows the save password prompt using the Google Identity API.
// *
// * @param activity The current ComponentActivity.
// * @param savePasswordRequest The save password request to be executed.
// * @param launchSavePasswordRequest The managed activity result launcher for the save password request.
// */
//fun showSavePasswordPrompt(
//    activity: ComponentActivity,
//    savePasswordRequest: SavePasswordRequest,
//    launchSavePasswordRequest: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
//) {
//    val credentialSavingClient = Identity
//        .getCredentialSavingClient(activity)
//
//    credentialSavingClient.savePassword(savePasswordRequest)
//        .addOnSuccessListener { result ->
//            Timber.tag(LOGIN_SCREEN_TAG).d("result is $result")
//
//            val intentSenderRequest =
//                IntentSenderRequest.Builder(result.pendingIntent)
//                    .build()
//
//            launchSavePasswordRequest.launch(intentSenderRequest)
//            activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
//
//            }
//        }
//}
