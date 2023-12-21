package com.example.demochatapplication.features.chat.ui.uistate

import androidx.compose.runtime.Stable
import com.example.demochatapplication.features.chat.domain.model.ChatScreenUiModel
import com.example.demochatapplication.features.shared.usersettings.UserSettings

sealed class ChatScreenState {
    data object Loading : ChatScreenState()
    data class Error(val errorMessage: String) : ChatScreenState()

    data class Success(
        val messages: List<ChatScreenUiModel.ChatModel> = emptyList(),
        val isSelectionModeEnabled: Boolean = false,
        val selectedMessages: Set<String> = emptySet(),
        val anotherUsername: String = "",
        val userSettings: UserSettings = UserSettings(),
        val sendTextMessageState: SendMessageScreenTextFieldState = SendMessageScreenTextFieldState(
            message =  "",
            placeholderValue = "Type...",
            label = "message",
        )
    ) :
        ChatScreenState()
}

