package com.example.demochatapplication.features.destinationswitcher.ui

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.demochatapplication.features.destinationswitcher.ui.components.ErrorComposable
import com.example.demochatapplication.features.destinationswitcher.ui.components.LoadingComposable
import com.example.demochatapplication.features.destinationswitcher.ui.utils.checkShouldRequestNotificationPermission
import com.example.demochatapplication.features.destinationswitcher.uistate.DestinationSwitcherScreenState
import com.example.demochatapplication.features.authentication.ui.login.utils.PaddingValues
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow

@SuppressLint("InlinedApi")
@Composable
fun DestinationSwitcherScreen(
    destinationSwitcherViewModel: DestinationSwitcherViewModel = hiltViewModel(),
    context: AppCompatActivity,
    navController: NavHostController,
) {
    val destinationSwitcherState = destinationSwitcherViewModel.destinationSwitcherState.value
    val showRational = rememberSaveable { mutableStateOf(false) }
    val launchActivityForResult =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                showRational.value = true
            } else {
                destinationSwitcherViewModel.onNotificationPermissionGranted()
            }
        }

    LaunchedEffect(key1 = Unit) {
        destinationSwitcherViewModel.uiEvents.consumeAsFlow().collectLatest {
            when (it) {
                is UiEvents.NavigateTo -> {
                    navController.popBackStack()
                    navController.navigate(it.destination)
                }

                UiEvents.RequestNotificationPermission -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        launchActivityForResult.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }

                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues.MEDIUM),
        contentAlignment = Alignment.Center
    ) {
        when (destinationSwitcherState) {
            is DestinationSwitcherScreenState.Error -> {
                ErrorComposable(
                    reason = (destinationSwitcherState).error?.message
                        ?: "Unable to load login status",
                    onRetryClicked = destinationSwitcherViewModel::onRetryButtonClicked
                )
            }

            DestinationSwitcherScreenState.Loading -> {
                LoadingComposable(
                    modifier = Modifier.fillMaxSize(),
                )
            }

            is DestinationSwitcherScreenState.Success -> {
                val shouldRequestForNotificationPermission =
                    checkShouldRequestNotificationPermission(
                        appLaunchStatus = destinationSwitcherState.appLaunchStatus,
                        context = context
                    )

                if (shouldRequestForNotificationPermission) {
                    val shouldShowRational =
                        context.shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)
                    if (shouldShowRational) {
                        showRational.value = true
                    } else run {
                        destinationSwitcherViewModel.onRequestNotificationLauncher()
                    }
                } else {
                    destinationSwitcherViewModel.onNotificationPermissionGranted()
                }

                if (showRational.value) {
                    RationalDialog(
                        modifier = Modifier
                            .padding(PaddingValues.MEDIUM)
                            .fillMaxWidth()
                    ) {
                        showRational.value = false
                        destinationSwitcherViewModel.onNotificationPermissionGranted()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RationalDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        text = {
               FlowRow(modifier = Modifier.fillMaxWidth()) {
                   Text(text = "Notification permission is denied. You will not get notification about new messages. Enable the notification permission from application settings to see notifications")
               }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "understood")
            }
        },
    )
}