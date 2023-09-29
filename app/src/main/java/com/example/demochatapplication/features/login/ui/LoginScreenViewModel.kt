package com.example.demochatapplication.features.login.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demochatapplication.features.login.core.UnsuccessfulLoginException
import com.example.demochatapplication.features.login.domain.model.SignInBodyEntity
import com.example.demochatapplication.features.login.domain.repository.IAuthenticationRepository
import com.example.demochatapplication.features.login.ui.uiState.LoginScreenState
import com.example.demochatapplication.features.login.ui.uiState.PasswordTextFieldProperties
import com.example.demochatapplication.features.shared.usersettings.UserSettings
import com.example.demochatapplication.features.shared.usersettings.UserSettingsSerializer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository,
) : ViewModel() {

    private val _loginScreenState = mutableStateOf(LoginScreenState())
    val loginScreenState: State<LoginScreenState> = _loginScreenState

    private val _passwordTextFieldProperties = mutableStateOf(PasswordTextFieldProperties())
    val passwordTextFieldProperties: State<PasswordTextFieldProperties> = _passwordTextFieldProperties

    fun onLoginButtonClicked(datastore: DataStore<UserSettings>) {
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
                    datastore = datastore,
                    username = _loginScreenState.value.usernameTextFieldState.text,
                    password = _loginScreenState.value.passwordTextFieldState.text,
                    token = signInUserResponseEntity.token,
                )

                Timber.tag(TAG).d("SignIn Response is $signInUserResponseEntity")
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

    private fun saveUserInfo(datastore: DataStore<UserSettings>, token: String, username: String, password: String,) {
        viewModelScope.launch {
            datastore.updateData {
                UserSettings(
                    username = username,
                    password = password,
                    token = token,
                )
            }

            datastore.data.collectLatest {
                Timber.tag(TAG).d("updated info: $it")
            }
        }
    }

    companion object {
        const val TAG = "loginscreenviewmodel"
        const val USER_SETTINGS_FILE_NAME = "user_settings.json"
    }
}