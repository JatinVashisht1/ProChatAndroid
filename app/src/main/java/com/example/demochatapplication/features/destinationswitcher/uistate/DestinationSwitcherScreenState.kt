package com.example.demochatapplication.features.destinationswitcher.uistate

sealed interface DestinationSwitcherScreenState {
    data object Loading: DestinationSwitcherScreenState
    data object Success: DestinationSwitcherScreenState
    data class Error(val error: Throwable?): DestinationSwitcherScreenState
}