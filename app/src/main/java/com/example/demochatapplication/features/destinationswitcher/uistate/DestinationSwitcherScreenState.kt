package com.example.demochatapplication.features.destinationswitcher.uistate

import com.example.demochatapplication.features.shared.applaunchstatus.AppLaunchStatus
import com.example.demochatapplication.features.shared.usersettings.UserSettings

sealed class DestinationSwitcherScreenState {
    data object Loading: DestinationSwitcherScreenState()
    data class Success(val userSettings: UserSettings, val appLaunchStatus: AppLaunchStatus) : DestinationSwitcherScreenState()
    data class Error(val error: Throwable?): DestinationSwitcherScreenState()
}