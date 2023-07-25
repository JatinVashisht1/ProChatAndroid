package com.example.demochatapplication.features.login.ui

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demochatapplication.features.login.core.UnsuccessfulLoginException
import com.example.demochatapplication.features.login.domain.model.SignInBodyEntity
import com.example.demochatapplication.features.login.domain.repository.IAuthenticationRepository
import com.example.demochatapplication.features.login.ui.uiState.LoginScreenState
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository,
) : ViewModel() {

    val loginScreenState = mutableStateOf(LoginScreenState())

    fun onLoginButtonClicked() {
        viewModelScope.launch {
            val username = loginScreenState.value.usernameTextFieldState.text
            val password = loginScreenState.value.passwordTextFieldState.text
            try {
                val signInUserResponseEntity = authenticationRepository.signInUser(
                    SignInBodyEntity(
                        username = username,
                        password = password
                    )
                )

                Timber.tag(TAG).d("SignIn Response is $signInUserResponseEntity")
            } catch (unsuccessfulLoginException: UnsuccessfulLoginException) {
                Timber.tag(TAG).d("Unable to login: ${unsuccessfulLoginException.message}")
            }
        }
    }

    fun onUsernameTextFieldChange(newUsernameString: String) {
        Timber.tag(TAG).d("new username string $newUsernameString")
        loginScreenState.value = loginScreenState.value.copy(
            usernameTextFieldState = loginScreenState.value.usernameTextFieldState.copy(text = newUsernameString)
        )
    }

    fun onPasswordTextFieldChange(newPasswordString: String) {
        loginScreenState.value = loginScreenState.value.copy(
            passwordTextFieldState = loginScreenState.value.passwordTextFieldState.copy(text = newPasswordString)
        )
    }

    companion object {
        const val TAG = "loginscreenviewmodel"
    }
}