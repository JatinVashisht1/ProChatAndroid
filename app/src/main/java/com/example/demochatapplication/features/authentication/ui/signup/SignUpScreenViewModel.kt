package com.example.demochatapplication.features.authentication.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demochatapplication.core.navigation.Destinations
import com.example.demochatapplication.features.authentication.core.UnSuccessfulSignUpException
import com.example.demochatapplication.features.authentication.domain.model.SignUpBodyModel
import com.example.demochatapplication.features.authentication.domain.repository.IAuthenticationRepository
import com.example.demochatapplication.features.authentication.ui.signup.uistate.SignUpScreenState
import com.example.demochatapplication.features.shared.usersettings.UserSettings
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignUpScreenViewModel @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository,
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {
    private val _signUpScreenState: MutableStateFlow<SignUpScreenState> =
        MutableStateFlow(SignUpScreenState.Success())
    val signUpScreenState: StateFlow<SignUpScreenState> get() = _signUpScreenState.asStateFlow()

    val signUpScreenUiEvents = Channel<SignUpScreenUiEvents>()

    fun onUsernameTextFieldValueChange(newUsername: String) {
        val currentScreenState = _signUpScreenState.value

        if (currentScreenState is SignUpScreenState.Success) {
            val currentUsernameTextFieldState = currentScreenState.usernameTextFieldState

            _signUpScreenState.value = currentScreenState.copy(
                usernameTextFieldState = currentUsernameTextFieldState.copy(text = newUsername)
            )
        }
    }

    fun onPasswordTextFieldValueChange(newPassword: String) {
        val currentScreenState = _signUpScreenState.value

        if (currentScreenState is SignUpScreenState.Success) {
            val currentPasswordTextFieldState = currentScreenState.passwordTextFieldState

            _signUpScreenState.value = currentScreenState.copy(
                passwordTextFieldState = currentPasswordTextFieldState.copy(text = newPassword)
            )
        }
    }

    fun onSignUpButtonClicked() {
        viewModelScope.launch {
            val currentScreenState = _signUpScreenState.value
            val firebaseRegistrationToken = FirebaseMessaging.getInstance().token.await()
            if (currentScreenState is SignUpScreenState.Success) {
                try {
                    val username = currentScreenState.usernameTextFieldState.text
                    val password = currentScreenState.passwordTextFieldState.text
                    val signUpBodyModel = SignUpBodyModel(
                        username = username,
                        password = username,
                        firebaseRegistrationToken = firebaseRegistrationToken
                    )
                    val signUpUserResponse =
                        authenticationRepository.signUpUser(signUpBodyModel = signUpBodyModel)
                    val userSettings = UserSettings(
                        username = username,
                        password = password,
                        token = signUpUserResponse.jwtToken,
                        firebaseRegistrationToken = firebaseRegistrationToken,
                    )

                    userSettingsRepository.writeUserSettings(userSettings = userSettings)

                    sendUiEvents(SignUpScreenUiEvents.NavigateTo(Destinations.AccountsScreen.route))

                } catch (e: UnSuccessfulSignUpException) {
                    Timber.tag(TAG).d("exception is $e")
                    sendUiEvents(
                        SignUpScreenUiEvents.ShowSnackBar(
                            e.message ?: "unable to sign you up, please try again or after sometime"
                        )
                    )
                } catch (e: Exception) {
                    Timber.tag(TAG).d("exception is $e")
                    sendUiEvents(SignUpScreenUiEvents.ShowSnackBar("unable to sign you up, please try again or after sometime"))
                }
            }
        }
    }

    private suspend fun sendUiEvents(uiEvent: SignUpScreenUiEvents) {
        signUpScreenUiEvents.send(uiEvent)
    }

    companion object {
        const val TAG = "signupscreenviewmodel"
    }
}

sealed class SignUpScreenUiEvents {
    data class ShowSnackBar(val message: String) : SignUpScreenUiEvents()
    data class NavigateTo(val route: String) : SignUpScreenUiEvents()
}