package com.example.demochatapplication.features.chat.ui.utils

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.example.demochatapplication.features.chat.domain.model.ChatScreenUiModel
import com.example.demochatapplication.features.chat.ui.uistate.ChatScreenState

data class ChatScreenContentParams(
    val modifier: Modifier = Modifier,
    val chatScreenState: ChatScreenState.Success,
    val lazyListState: LazyListState,
    val interactionSource: MutableInteractionSource,
    val textMessages: LazyPagingItems<ChatScreenUiModel>,
)
