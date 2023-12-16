package com.example.demochatapplication.features.destinationswitcher.ui

import android.os.Build
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demochatapplication.core.navigation.Destinations
import com.example.demochatapplication.features.destinationswitcher.ui.utils.checkShouldNavigateToAccountScreen
import com.example.demochatapplication.features.destinationswitcher.uistate.DestinationSwitcherScreenState
import com.example.demochatapplication.features.shared.applaunchstatus.AppLaunchStatus
import com.example.demochatapplication.features.shared.applaunchstatus.AppLaunchStatusRepository
import com.example.demochatapplication.features.shared.usersettings.UserSettings
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel()
class DestinationSwitcherViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
    private val appLaunchStatusRepository: AppLaunchStatusRepository,
) : ViewModel() {
    private val _destinationScreenState: MutableState<DestinationSwitcherScreenState> =
        mutableStateOf(DestinationSwitcherScreenState.Loading)

    val destinationSwitcherState: State<DestinationSwitcherScreenState> = _destinationScreenState
    val uiEvents: Channel<UiEvents> = Channel<UiEvents>()
    private var checkLastTimePermissionCheckJob: Job? = null
    private var checkLoginStatusJob: Job? = null

    private val _userCredentials = MutableStateFlow(UserSettings())
    val userCredentials: StateFlow<UserSettings> get() = _userCredentials.asStateFlow()

    init {
        viewModelScope.launch {
            checkLastTimePermissionCheckJob?.cancel()
            checkLoginStatusJob?.cancel()

            checkLoginStatusJob = launch {
                checkLoginStatus()
            }
        }
    }

    private suspend fun checkLoginStatus() {
        _destinationScreenState.value = DestinationSwitcherScreenState.Loading
        try {
            val userSettings = userSettingsRepository.getFirstEntry()
            _userCredentials.value = userSettings
            val appLaunchStatus = appLaunchStatusRepository.getFirstEntry()

            _destinationScreenState.value = DestinationSwitcherScreenState.Success(userSettings = userSettings, appLaunchStatus = appLaunchStatus)

        } catch (e: Exception) {
            _destinationScreenState.value =
                DestinationSwitcherScreenState.Error(Throwable("Unable to load login status"))
        }
    }

    fun onRequestNotificationLauncher () {
        sendUiEvents(UiEvents.RequestNotificationPermission)
        viewModelScope.launch {
            val appLaunchStatus = AppLaunchStatus(lastTimePermissionCheckedMillis = System.currentTimeMillis())
            appLaunchStatusRepository.updateAppLaunchStatus(appLaunchStatus = appLaunchStatus)
        }
    }

    fun onNotificationPermissionGranted () {
        Timber.tag(TAG).d("current credentials are: ${_userCredentials.value}")
        val shouldNavigateToAccountsScreen = checkShouldNavigateToAccountScreen(_userCredentials.value)
        val navigationString = if (shouldNavigateToAccountsScreen) Destinations.AccountsScreen.route else Destinations.LoginScreen.route
        sendUiEvents(UiEvents.NavigateTo(navigationString))
    }


    private fun sendUiEvents(uiEvent: UiEvents) {
        viewModelScope.launch {
            when (uiEvent) {
                is UiEvents.NavigateTo -> {
                    uiEvents.send(uiEvent)
                }

                UiEvents.RequestNotificationPermission -> {
                    uiEvents.send(uiEvent)
                }
            }
        }
    }

    private suspend fun checkShouldRequestNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return false
        val appLaunchStatus = appLaunchStatusRepository.getFirstEntry()
        val lastTimePermissionCheck = appLaunchStatus.lastTimePermissionCheckedMillis
        val currentTimeMillis = System.currentTimeMillis()
        val millisDifferencePermissionChecked =
            currentTimeMillis.minus(lastTimePermissionCheck).toDouble()
        val daysDifferencePermissionChecked =
            /*                                         seconds       minutes        hours        days  */
            (millisDifferencePermissionChecked.div(1000).div(60).div(60).div(24)).toInt()

        return daysDifferencePermissionChecked >= 3
    }

    fun onRetryButtonClicked() {
        viewModelScope.launch {
            checkLoginStatus()
        }
    }

    companion object {
        const val TAG = "destinationswitcherviewmodel"
    }
}

sealed interface UiEvents {
    data class NavigateTo(val destination: String) : UiEvents
    data object RequestNotificationPermission : UiEvents
}