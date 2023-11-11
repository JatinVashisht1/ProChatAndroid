package com.example.demochatapplication.features.destinationswitcher.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.demochatapplication.features.destinationswitcher.ui.components.ErrorComposable
import com.example.demochatapplication.features.destinationswitcher.ui.components.LoadingComposable
import com.example.demochatapplication.features.destinationswitcher.uistate.DestinationSwitcherScreenState
import com.example.demochatapplication.features.login.ui.utils.PaddingValues
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow

@Composable
fun DestinationSwitcherScreen(
    destinationSwitcherViewModel: DestinationSwitcherViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val destinationSwitcherState = destinationSwitcherViewModel.destinationSwitcherState.value
    LaunchedEffect(key1 = Unit) {
        destinationSwitcherViewModel.uiEvents.consumeAsFlow().collectLatest {
            when (it) {
                is UiEvents.NavigateTo -> {
//                    delay(5000)
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
        when (destinationSwitcherState) {
            is DestinationSwitcherScreenState.Error -> {
                ErrorComposable(
                    reason = (destinationSwitcherState).error?.message
                        ?: "Unable to load login status",
                    onRetryClicked = destinationSwitcherViewModel::onRetryButtonClicked
                )
            }

            DestinationSwitcherScreenState.Loading,
            DestinationSwitcherScreenState.Success -> {
                LoadingComposable(
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}