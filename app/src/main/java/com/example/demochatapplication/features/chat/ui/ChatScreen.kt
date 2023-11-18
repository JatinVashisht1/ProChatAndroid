package com.example.demochatapplication.features.chat.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.demochatapplication.features.chat.domain.model.ChatModel
import com.example.demochatapplication.features.chat.ui.components.ChatMessageCard
import com.example.demochatapplication.features.chat.ui.components.SendMessageTextField
import com.example.demochatapplication.features.chat.ui.uistate.ChatScreenState
import com.example.demochatapplication.features.chat.ui.uistate.SendMessageTextFieldState
import com.example.demochatapplication.features.chat.ui.utils.CornerRoundnessDpValues
import com.example.demochatapplication.features.login.ui.utils.PaddingValues
import com.example.demochatapplication.ui.theme.DarkMessageCardBackgroundSender
import timber.log.Timber

@Composable
fun ChatScreen(
    chatScreenViewModel: ChatScreenViewModel = hiltViewModel(),
) {
    val userCredentials = chatScreenViewModel.userSettingsStateFlow.collectAsState().value
    val sendMessageTextFieldState by chatScreenViewModel.sendMessageTextFieldState.collectAsState()
    val textMessages = chatScreenViewModel.textMessageListState.collectAsLazyPagingItems()
    val anotherUsername = chatScreenViewModel.anotherUsernameState.collectAsState().value
    val chatMessagesListState = rememberLazyListState()

    SideEffect {
        Timber.tag(TAG).d("text messages count: ${textMessages.itemCount}")
    }


    Surface(color = MaterialTheme.colors.background) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (chatScreenViewModel.chatScreenState.collectAsState().value) {
                is ChatScreenState.Error -> {
                    Text("Error")
                }

                ChatScreenState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.size(100.dp))
                }

                is ChatScreenState.Success -> {

                    when (textMessages.loadState.refresh) {
                        is LoadState.Error -> {

                        }

                        LoadState.Loading -> {

                        }

                        is LoadState.NotLoading -> {
                            ChatScreenContent(
                                textFieldState = sendMessageTextFieldState,
                                onTypingMessageValueChange = chatScreenViewModel::onSendTextFieldValueChange,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(PaddingValues.MEDIUM),
                                onSendTextMessageButtonClicked = chatScreenViewModel::onSendChatMessageClicked,
                                textMessages = textMessages,
                                username = userCredentials.username,
                                anotherUsername = anotherUsername,
                                chatMessagesListState = chatMessagesListState,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatScreenContent(
    modifier: Modifier = Modifier,
    username: String,
    textFieldState: SendMessageTextFieldState,
    onTypingMessageValueChange: (String) -> Unit,
    onSendTextMessageButtonClicked: () -> Unit,
    textMessages: LazyPagingItems<ChatModel>,
    anotherUsername: String,
    chatMessagesListState: LazyListState = rememberLazyListState(),
) {
    val roundedCornerDpValues = remember {
        CornerRoundnessDpValues().getSimpleRoundedCornerValues()
    }


    Column(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f),
            state = chatMessagesListState,
        ) {
            items(count = textMessages.itemCount) { index ->
                textMessages[index]?.let {
                    ChatMessageCard(
                        chatModel = it,
                        modifier = Modifier
                            .padding(top = PaddingValues.MEDIUM)
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        senderBackgroundColor = MaterialTheme.colors.primary,
                        receiverBackgroundColor = DarkMessageCardBackgroundSender,
                        username = username,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(PaddingValues.MEDIUM))

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = PaddingValues.MEDIUM),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            SendMessageTextField(
                textFieldState = textFieldState,
                onTypingMessageValueChange = onTypingMessageValueChange,
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .heightIn(min = 40.dp)
//                    .weight(weight = 3f, fill = true),
            )

            Spacer(modifier = Modifier.width(PaddingValues.MEDIUM))

            FloatingActionButton(
                onClick = onSendTextMessageButtonClicked,
                modifier = Modifier
//                    .weight(weight = 1f, fill = true)
                    .fillMaxWidth()
                    .clip(CircleShape),
                backgroundColor = MaterialTheme.colors.primary,
            ) {
                Icon(imageVector = Icons.Filled.Send, contentDescription = "Send Message")
            }
        }
    }
}

private val TAG = "chatscreentag"