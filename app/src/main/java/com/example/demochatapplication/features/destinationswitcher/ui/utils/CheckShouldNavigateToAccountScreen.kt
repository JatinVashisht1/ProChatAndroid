package com.example.demochatapplication.features.destinationswitcher.ui.utils

import com.example.demochatapplication.features.shared.usersettings.UserSettings

fun checkShouldNavigateToAccountScreen(userSettings: UserSettings): Boolean {
    return (userSettings.token.isNotBlank() && userSettings.username.isNotBlank() && userSettings.password.isNotBlank())
}