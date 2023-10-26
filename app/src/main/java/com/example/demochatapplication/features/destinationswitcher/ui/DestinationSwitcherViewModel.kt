package com.example.demochatapplication.features.destinationswitcher.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demochatapplication.features.destinationswitcher.uistate.DestinationSwitcherScreenState
import com.example.demochatapplication.features.login.ui.LoginScreenViewModel
import com.example.demochatapplication.features.shared.navigation.Destinations
import com.example.demochatapplication.features.shared.usersettings.UserSettings
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel()
class DestinationSwitcherViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
) : ViewModel() {
    private val _destinationScreenState: MutableState<DestinationSwitcherScreenState> =
        mutableStateOf(DestinationSwitcherScreenState.Loading)

    val destinationSwitcherState: State<DestinationSwitcherScreenState> = _destinationScreenState

    val uiEvents: Channel<UiEvents> = Channel<UiEvents>()

    init {
        checkLoginStatus()
    }
    private fun checkLoginStatus() {
        _destinationScreenState.value = DestinationSwitcherScreenState.Loading
        viewModelScope.launch {
            try {
//                val userSettings = userSettingsRepository.getFirstEntry()
                userSettingsRepository.userSettings.collectLatest { userSettings->
                    Timber.tag(TAG).d("user settings are: $userSettings")
                    _destinationScreenState.value = DestinationSwitcherScreenState.Success
                    if (userSettings.username.isNotBlank() && userSettings.password.isNotBlank() && userSettings.token.isNotBlank()) {
//                        sendUiEvents(UiEvents.NavigateTo(Destinations.ChatScreen.route))
                        sendUiEvents(UiEvents.NavigateTo(Destinations.AccountsScreen.route))
                    } else {
                        sendUiEvents(UiEvents.NavigateTo(Destinations.LoginScreen.route))
                    }
                }
            } catch (e: Exception) {
                Timber.tag(TAG).d(e)
                _destinationScreenState.value = DestinationSwitcherScreenState.Error(Throwable("Unable to load login status"))
            }
        }
    }

    private fun sendUiEvents(uiEvent: UiEvents) {
        viewModelScope.launch {
            when(uiEvent) {
                is UiEvents.NavigateTo -> {
                    uiEvents.send(uiEvent)
                }
            }
        }
    }

    companion object {
        const val TAG = "destinationswitcherviewmodel"
    }
}

sealed interface UiEvents {
    data class NavigateTo(val destination: String) : UiEvents
}