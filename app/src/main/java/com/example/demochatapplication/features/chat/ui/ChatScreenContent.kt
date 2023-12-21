package com.example.demochatapplication.features.chat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import com.example.demochatapplication.features.chat.ui.components.ChatInputSection
import com.example.demochatapplication.features.chat.ui.components.ChatMessagesSection
import com.example.demochatapplication.features.chat.ui.components.ChatTopBar
import com.example.demochatapplication.features.chat.ui.utils.ChatScreenContentParams


@Composable
fun ChatScreenContent(
    chatScreenContentParams: ChatScreenContentParams,
    onTypingMessageValueChange: (String) -> Unit,
    onSendTextMessageButtonClicked: () -> Unit,
    onMessageClicked: (messageId: String) -> Unit,
    onMessageLongClicked: (messageId: String) -> Unit,
    onDeleteMessagesClicked: () -> Unit,
) {
    Scaffold(
        topBar = { ChatTopBar(chatScreenContentParams, onDeleteMessagesClicked) }
    ) {
        Column(
            modifier = chatScreenContentParams.modifier.padding(it),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ChatInputSection(
                chatScreenState = chatScreenContentParams.chatScreenState,
                onTypingMessageValueChange = onTypingMessageValueChange,
                onSendTextMessageButtonClicked = onSendTextMessageButtonClicked
            )

            ChatMessagesSection(
                chatScreenContentParams = chatScreenContentParams,
                onMessageClicked = onMessageClicked,
                onMessageLongClicked = onMessageLongClicked
            )
        }
    }
}