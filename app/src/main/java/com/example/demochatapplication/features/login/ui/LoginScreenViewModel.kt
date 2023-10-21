package com.example.demochatapplication.features.login.ui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demochatapplication.features.login.core.UnsuccessfulLoginException
import com.example.demochatapplication.features.login.domain.model.SignInBodyEntity
import com.example.demochatapplication.features.login.domain.repository.IAuthenticationRepository
import com.example.demochatapplication.features.login.ui.uistate.LoginScreenState
import com.example.demochatapplication.features.login.ui.uistate.PasswordTextFieldProperties
import com.example.demochatapplication.features.shared.navigation.Destinations
import com.example.demochatapplication.features.shared.usersettings.UserSettings
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository,
    private val userSettingsRepository: UserSettingsRepository,
) : ViewModel() {

    private val _loginScreenState = mutableStateOf(LoginScreenState())
    val loginScreenState: State<LoginScreenState> = _loginScreenState

    private val _passwordTextFieldProperties = mutableStateOf(PasswordTextFieldProperties())
    val passwordTextFieldProperties: State<PasswordTextFieldProperties> = _passwordTextFieldProperties

    val uiEvents = Channel<UiEvents>()

    fun onLoginButtonClicked() {
        viewModelScope.launch {
            val username = _loginScreenState.value.usernameTextFieldState.text
            val password = _loginScreenState.value.passwordTextFieldState.text
            try {
                val signInUserResponseEntity = authenticationRepository.signInUser(
                    SignInBodyEntity(
                        username = username,
                        password = password,
                    )
                )

                saveUserInfo(
                    username = _loginScreenState.value.usernameTextFieldState.text,
                    password = _loginScreenState.value.passwordTextFieldState.text,
                    token = signInUserResponseEntity.token,
                )

                Timber.tag(TAG).d("SignIn Response is $signInUserResponseEntity")

                onEvent(UiEvents.NavigateTo(Destinations.ChatScreen.route))

            } catch (unsuccessfulLoginException: UnsuccessfulLoginException) {
                Timber.tag(TAG).d("Unable to login: ${unsuccessfulLoginException.message}")
            }
        }
    }

    fun onUsernameTextFieldChange(newUsernameString: String) {
        Timber.tag(TAG).d("new username string $newUsernameString")
        _loginScreenState.value = _loginScreenState.value.copy(
            usernameTextFieldState = _loginScreenState.value.usernameTextFieldState.copy(text = newUsernameString)
        )
    }

    fun onPasswordTextFieldChange(newPasswordString: String) {
        _loginScreenState.value = _loginScreenState.value.copy(
            passwordTextFieldState = _loginScreenState.value.passwordTextFieldState.copy(text = newPasswordString)
        )
    }

    private fun saveUserInfo(token: String, username: String, password: String,) {
//        Timber.tag(TAG).d("entered save use info function")
        viewModelScope.launch {
            userSettingsRepository.writeUserSettings(UserSettings(username = username, password = password, token = token))

            userSettingsRepository.userSettings.collectLatest {
                Timber.tag(TAG).d("user settings are $it")
            }

        }
    }

    private fun onEvent(event: UiEvents) {
        viewModelScope.launch {
            uiEvents.send(event)
        }
    }

    companion object {
        const val TAG = "loginscreenviewmodel"
    }
}

sealed interface UiEvents {
    data class NavigateTo(val destination: String): UiEvents
}