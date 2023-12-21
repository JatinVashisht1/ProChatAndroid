package com.example.demochatapplication.features.authentication.ui.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demochatapplication.core.navigation.Destinations
import com.example.demochatapplication.features.authentication.core.UnsuccessfulLoginException
import com.example.demochatapplication.features.authentication.domain.model.SignInBodyEntity
import com.example.demochatapplication.features.authentication.domain.repository.IAuthenticationRepository
import com.example.demochatapplication.features.authentication.ui.uistate.LoginScreenState
import com.example.demochatapplication.features.authentication.ui.uistate.PasswordTextFieldProperties
import com.example.demochatapplication.features.shared.usersettings.UserSettings
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
    val passwordTextFieldProperties: State<PasswordTextFieldProperties> =
        _passwordTextFieldProperties

    val uiEvents = Channel<UiEvents>()

    fun onLoginButtonClicked() {
        viewModelScope.launch(IO) {
            val username = _loginScreenState.value.usernameTextFieldState.text
            val password = _loginScreenState.value.passwordTextFieldState.text
            val firebaseRegistrationToken = FirebaseMessaging.getInstance().token.await()
            Timber.tag(TAG).d("firebase registration token is $firebaseRegistrationToken")

            try {
                val signInUserResponseEntity = authenticationRepository.signInUser(
                    SignInBodyEntity(
                        username = username,
                        password = password,
                        firebaseRegistrationToken = firebaseRegistrationToken,
                    )
                )

                saveUserInfo(
                    username = _loginScreenState.value.usernameTextFieldState.text,
                    password = _loginScreenState.value.passwordTextFieldState.text,
                    token = signInUserResponseEntity.token,
                    firebaseRegistrationToken = firebaseRegistrationToken
                )

                Timber.tag(TAG).d("SignIn Response is $signInUserResponseEntity")

                onEvent(UiEvents.NavigateTo(destination = Destinations.AccountsScreen.route, shouldPopBackStack = true))

            } catch (unsuccessfulLoginException: UnsuccessfulLoginException) {
                Timber.tag(TAG).d("Unable to login: ${unsuccessfulLoginException.message}")
                onEvent(UiEvents.ShowSnackbar(unsuccessfulLoginException.message?: "Unable to login at the moment. Please try again later"))
            } catch (e: Exception) {
                onEvent(UiEvents.ShowSnackbar("Unable to login at the moment. Please try again later"))
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

    fun onNavigateToSignUpButtonClicked () {
        viewModelScope.launch {
            onEvent(UiEvents.NavigateTo(Destinations.SignUpScreen.route))
        }
    }

    private suspend fun saveUserInfo(
        token: String,
        username: String,
        password: String,
        firebaseRegistrationToken: String,
    ) {
        userSettingsRepository.writeUserSettings(
            UserSettings(
                username = username,
                password = password,
                token = token,
                firebaseRegistrationToken = firebaseRegistrationToken,
            )
        )
    }

    private suspend fun onEvent(event: UiEvents) {
        uiEvents.send(event)
    }

    companion object {
        const val TAG = "loginscreenviewmodel"
    }
}

sealed interface UiEvents {
    data class NavigateTo(val destination: String, val shouldPopBackStack: Boolean = false) : UiEvents
    data class ShowSnackbar(val message: String): UiEvents
}