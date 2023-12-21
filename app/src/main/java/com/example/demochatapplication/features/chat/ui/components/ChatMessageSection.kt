package com.example.demochatapplication.features.chat.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import com.example.demochatapplication.features.authentication.ui.login.utils.PaddingValues
import com.example.demochatapplication.features.chat.domain.model.ChatScreenUiModel
import com.example.demochatapplication.features.chat.ui.utils.ChatScreenContentParams
import com.example.demochatapplication.features.shared.composables.LoadingComposable


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatMessagesSection(
    chatScreenContentParams: ChatScreenContentParams,
    onMessageClicked: (messageId: String) -> Unit,
    onMessageLongClicked: (messageId: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = chatScreenContentParams.lazyListState,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(count = chatScreenContentParams.textMessages.itemCount) { index ->
            chatScreenContentParams.textMessages[index]?.let { chatScreenUiModel ->
                when (chatScreenUiModel) {
                    is ChatScreenUiModel.ChatModel -> {
                        if (!(chatScreenUiModel.deletedByReceiver && chatScreenUiModel.to == chatScreenContentParams.chatScreenState.userSettings.username)) {
                            ChatMessageCard(
                                chatModel = chatScreenUiModel,
                                modifier = Modifier
                                    .padding(top = PaddingValues.MEDIUM)
                                    .background(
                                        if (chatScreenContentParams.chatScreenState.selectedMessages.contains(
                                                chatScreenUiModel.id
                                            )
                                        ) Color.Blue.copy(alpha = 0.4f) else Color.Transparent
                                    )
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .rotate(180f)
                                    .combinedClickable(
                                        interactionSource = chatScreenContentParams.interactionSource,
                                        indication = rememberRipple(color = Color.Green),
                                        onClick = { onMessageClicked(chatScreenUiModel.id) },
                                        onLongClick = { onMessageLongClicked(chatScreenUiModel.id) }
                                    ),
                                senderBackgroundColor = MaterialTheme.colors.onBackground,
                                receiverBackgroundColor = MaterialTheme.colors.primary,
                                username = chatScreenContentParams.chatScreenState.userSettings.username,
                            )
                        }
                    }

                    is ChatScreenUiModel.UnreadMessagesModel -> {
                        Text(
                            text = chatScreenUiModel.data,
                            modifier = Modifier
                                .padding(vertical = PaddingValues.MEDIUM)
                                .rotate(180f)
                        )
                    }
                }
            }
        }

        if (chatScreenContentParams.textMessages.loadState.append == LoadState.Loading) {
            item {
                LoadingComposable(
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}