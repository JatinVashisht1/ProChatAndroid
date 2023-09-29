package com.example.demochatapplication.features.shared.navigation

sealed class Destinations(val route: String) {
    data object LoginScreen: Destinations("LoginScreen")
    data object ChatScreen: Destinations("ChatScreen")
    data object DestinationSwitcher: Destinations("DestinationSwitcher")
}
