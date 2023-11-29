package com.example.demochatapplication.features.chat.ui.uistate

import com.example.demochatapplication.features.chat.domain.model.ChatScreenUiModel

sealed class ChatScreenState {
    data object Loading : ChatScreenState()
    data class Error(val errorMessage: String) : ChatScreenState()
    data class Success(val messages: List<ChatScreenUiModel.ChatModel> = emptyList()) :
        ChatScreenState()
}

