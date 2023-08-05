package com.example.demochatapplication.features.login.ui

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demochatapplication.features.login.core.UnsuccessfulLoginException
import com.example.demochatapplication.features.login.domain.model.SignInBodyEntity
import com.example.demochatapplication.features.login.domain.repository.IAuthenticationRepository
import com.example.demochatapplication.features.login.ui.uiState.LoginScreenState
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SavePasswordRequest
import com.google.android.gms.auth.api.identity.SignInPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository,
    private val chatApplication: Application,
) : ViewModel() {

    val loginScreenState = mutableStateOf(LoginScreenState())

    private val _showSavePasswordOneTapUi = mutableStateOf(false)
    val showSavePasswordOneTapUi: State<Boolean> = _showSavePasswordOneTapUi

    private val _getPassword = mutableStateOf(true)
    val getPassword: State<Boolean> = _getPassword

    val signInRequest: BeginSignInRequest = BeginSignInRequest.builder()
        .setPasswordRequestOptions(
            BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build()
        )
        // Automatically sign in when exactly one credential is retrieved.
        .setAutoSelectEnabled(true)
        .build()

    val signInClient = Identity.getSignInClient(chatApplication)

    fun onShowHintPickerEventOver() {
        _showSavePasswordOneTapUi.value = false
    }

    fun getSavePasswordRequest(username: String, password: String): SavePasswordRequest {
        val signInPassword = SignInPassword(username, password);
        val savePasswordRequest =
            SavePasswordRequest.builder()
                .setSignInPassword(signInPassword)
                .build()

        return savePasswordRequest
    }

    fun onLoginButtonClicked() {
        viewModelScope.launch {
            _showSavePasswordOneTapUi.value = true
            val username = loginScreenState.value.usernameTextFieldState.text
            val password = loginScreenState.value.passwordTextFieldState.text
            try {
                val signInUserResponseEntity = authenticationRepository.signInUser(
                    SignInBodyEntity(
                        username = username,
                        password = password,
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

    fun onGetPasswordButtonClicked() {
        _getPassword.value = true
    }

    fun onSavePasswordRequestComplete() {
        _showSavePasswordOneTapUi.value = false
    }

    fun onPasswordGettingCompleted() {
        _getPassword.value = false
    }

    companion object {
        const val TAG = "loginscreenviewmodel"
        const val REQUEST_CODE_GIS_SAVE_PASSWORD = 2 /* unique request id */
    }
}