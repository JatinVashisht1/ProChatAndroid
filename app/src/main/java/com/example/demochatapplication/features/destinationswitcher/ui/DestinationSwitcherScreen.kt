package com.example.demochatapplication.features.destinationswitcher.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.navOptions
import com.example.demochatapplication.features.destinationswitcher.ui.components.ErrorComposable
import com.example.demochatapplication.features.destinationswitcher.ui.components.LoadingComposable
import com.example.demochatapplication.features.destinationswitcher.uistate.DestinationSwitcherScreenState
import com.example.demochatapplication.features.login.ui.utils.PaddingValues
import com.example.demochatapplication.features.shared.navigation.Destinations
import com.example.demochatapplication.features.shared.usersettings.UserSettings
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun DestinationSwitcherScreen(
    destinationSwitcherViewModel: DestinationSwitcherViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val destinationSwitcherState = destinationSwitcherViewModel.destinationSwitcherState.value
//    Timber.tag("destinationswitcherscreen").d(datastore.readBytes().decodeToString())

    LaunchedEffect(key1 = Unit) {
        destinationSwitcherViewModel.uiEvents.consumeAsFlow().collectLatest {
            when(it) {
                is UiEvents.NavigateTo -> {
                    navController.popBackStack()
                    navController.navigate(it.destination)
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
        when(destinationSwitcherState) {
            is DestinationSwitcherScreenState.Error -> {
                ErrorComposable(
                    reason = (destinationSwitcherState).error?.message
                        ?: "Unable to load login status"
                )
            }
            DestinationSwitcherScreenState.Loading -> {
                CircularProgressIndicator()
            }
            DestinationSwitcherScreenState.Success -> {
                ErrorComposable(reason = "Testing")
            }
        }
    }
}