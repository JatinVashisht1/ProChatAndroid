package com.example.demochatapplication.core.navigation

sealed class Destinations(val route: String) {
    data object LoginScreen: Destinations("LoginScreen")
    data object ChatScreen: Destinations("ChatScreen")
    data object DestinationSwitcher: Destinations("DestinationSwitcher")
    data object AccountsScreen: Destinations("AccountsScreen")
    data object SearchUserScreen: Destinations("SearchUserScreen")

    data object SignUpScreen: Destinations("signupscreen")
}
