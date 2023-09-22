package com.example.demochatapplication.features.login.ui.components
//
//import android.content.IntentSender
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.ManagedActivityResultLauncher
//import androidx.activity.result.ActivityResult
//import androidx.activity.result.IntentSenderRequest
//import com.google.android.gms.auth.api.identity.BeginSignInRequest
//import com.google.android.gms.auth.api.identity.SignInClient
//import timber.log.Timber
//
//fun beginSignIn(
//    oneTapClient: SignInClient,
//    signInRequest: BeginSignInRequest,
//    activity: ComponentActivity,
//    launchActivityForResult: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
//) {
//    oneTapClient.beginSignIn(signInRequest)
//        .addOnSuccessListener(activity) { result ->
//            try {
//                val intentSenderRequest =
//                    IntentSenderRequest.Builder(result.pendingIntent)
//                        .build()
//                launchActivityForResult.launch(intentSenderRequest)
//
//            } catch (e: IntentSender.SendIntentException) {
//                Timber.tag(LOGIN_SCREEN_TAG)
//                    .e("Couldn't start One Tap UI: %s", e.localizedMessage)
//            }
//        }
//        .addOnFailureListener(activity) { e ->
//            // No saved credentials found. Launch the One Tap sign-up flow, or
//            // do nothing and continue presenting the signed-out UI.
//            Timber.tag(LOGIN_SCREEN_TAG).d(e.localizedMessage)
//
//        }
//}