package com.example.demochatapplication.features.destinationswitcher.ui

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow

@Composable
fun DestinationSwitcherScreen(
    destinationSwitcherViewModel: DestinationSwitcherViewModel = hiltViewModel(),
    navController: NavHostController
) {
    
    val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
    val color by infiniteTransition.animateColor(
        initialValue = Color.Red,
        targetValue = Color.Yellow,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 1500, easing = LinearEasing)
        ), label = ""
    )
    
    Box(modifier = Modifier
        .fillMaxSize()
        .drawBehind { drawRect(color) }
    )
    
    /*
    val destinationSwitcherState = destinationSwitcherViewModel.destinationSwitcherState.value
//    Timber.tag("destinationswitcherscreen").d(datastore.readBytes().decodeToString())
    val animatedFloat = remember { Animatable(0f) }
    LaunchedEffect(key1 = Unit) {

        delay(200) // to avoid repeated delays
        animatedFloat.animateTo(
            targetValue = 100f, animationSpec = infiniteRepeatable(
                animation = tween(700, easing =  FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

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
                        ?: "Unable to load login status",
                    onRetryClicked = destinationSwitcherViewModel::onRetryButtonClicked
                )
            }
            DestinationSwitcherScreenState.Loading -> {
                LoadingComposable(
                    modifier = Modifier.fillMaxSize(),
                    animatedFloat = animatedFloat.value,
                )
            }
            DestinationSwitcherScreenState.Success -> {
                LoadingComposable(
                    modifier = Modifier.fillMaxSize(),
                    animatedFloat = animatedFloat.value,
                )
            }
        }
    }
    
     */
}