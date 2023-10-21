package com.example.demochatapplication.features.chat.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demochatapplication.features.chat.domain.model.ChatModel
import com.example.demochatapplication.features.chat.ui.uistate.ChatScreenState
import com.example.demochatapplication.features.chat.ui.uistate.SendMessageTextFieldState
import com.example.demochatapplication.features.shared.usersettings.UserSettings
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel()
class ChatScreenViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
) : ViewModel() {
    private val _userSettingsStateFlow = MutableStateFlow<UserSettings>(UserSettings())
    val userSettingsStateFlow: StateFlow<UserSettings> = _userSettingsStateFlow.asStateFlow()

    private val _chatScreenState = MutableStateFlow<ChatScreenState>(ChatScreenState.Loading)
    val chatScreenState: StateFlow<ChatScreenState> = _chatScreenState

    private val _sendMessageTextFieldState =
        MutableStateFlow<SendMessageTextFieldState>(SendMessageTextFieldState())
    val sendMessageTextFieldState: StateFlow<SendMessageTextFieldState> get() = _sendMessageTextFieldState

    private val _textMessagesListState = mutableStateListOf<ChatModel>()
    val textMessageListState get() = _textMessagesListState.toList()


    init {
        fetchUserCredentials()
    }

    private fun fetchUserCredentials() {
        viewModelScope.launch {
            userSettingsRepository.userSettings.collectLatest { userSettings ->
                _userSettingsStateFlow.value = userSettings
                _chatScreenState.value = ChatScreenState.Success()
            }
        }
    }

    fun addRandomItem() {
        _textMessagesListState.add(ChatModel("def", "to", "message"))
    }

    fun onSendTextFieldValueChange(newValue: String) {
        Timber.tag(TAG).d("chat screen view model $newValue")
        _sendMessageTextFieldState.value = _sendMessageTextFieldState.value.copy(message = newValue)
    }

    companion object {
        const val TAG = "chatscreenviewmodel"
    }
}