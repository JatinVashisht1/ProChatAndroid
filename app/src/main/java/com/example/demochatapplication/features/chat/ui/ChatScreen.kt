@file:OptIn(ExperimentalFoundationApi::class)

package com.example.demochatapplication.features.chat.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.demochatapplication.features.authentication.ui.login.utils.PaddingValues
import com.example.demochatapplication.features.chat.domain.model.ChatScreenUiModel
import com.example.demochatapplication.features.chat.ui.components.ChatInputSection
import com.example.demochatapplication.features.chat.ui.components.ChatMessageCard
import com.example.demochatapplication.features.chat.ui.components.ChatMessagesSection
import com.example.demochatapplication.features.chat.ui.components.ChatTopBar
import com.example.demochatapplication.features.chat.ui.components.SendMessageTextField
import com.example.demochatapplication.features.chat.ui.uistate.ChatScreenState
import com.example.demochatapplication.features.chat.ui.utils.ChatScreenContentParams
import com.example.demochatapplication.features.shared.composables.ErrorComposable
import com.example.demochatapplication.features.shared.composables.LoadingComposable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatScreenViewModel: ChatScreenViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val textMessages = chatScreenViewModel.textMessageListState.collectAsLazyPagingItems()
    val chatScreenState = chatScreenViewModel.chatScreenState.collectAsState().value
    val lazyListState = rememberLazyListState()
    val interactionSource = remember { MutableInteractionSource() }

    SideEffect {
        Timber.tag(TAG).d("text messages count: ${textMessages.itemCount}")
    }

    LaunchedEffect(key1 = Unit) {
        chatScreenViewModel.chatScreenUiEvents.consumeAsFlow().collectLatest {
            when (it) {
                ChatScreenUiEvents.NavigateToFirstElement -> {
                    lazyListState.animateScrollToItem(index = 0)
                }

                ChatScreenUiEvents.NavigateUp -> {
                    navController.navigateUp()
                }
            }
        }
    }

    BackHandler(onBack = chatScreenViewModel::onBackButtonPressed)


    Surface(color = MaterialTheme.colors.background) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (chatScreenState) {
                is ChatScreenState.Error -> {
                    ErrorComposable(error = chatScreenState.errorMessage)
                }

                ChatScreenState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.size(100.dp))
                }

                is ChatScreenState.Success -> {
                    when (textMessages.loadState.refresh) {
                        is LoadState.Error -> {
                            ErrorComposable(error = "unable to load chat messages!")
                        }

                        LoadState.Loading -> {
                            LoadingComposable(modifier = Modifier.size(32.dp))
                        }

                        is LoadState.NotLoading -> {

                            val chatScreenContentParams = ChatScreenContentParams(
                                chatScreenState = chatScreenState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(PaddingValues.MEDIUM)
                                    .rotate(180f),
                                interactionSource = interactionSource,
                                lazyListState = lazyListState,
                                textMessages = textMessages,
                            )

                            ChatScreenContent(
                                chatScreenContentParams = chatScreenContentParams,
                                onSendTextMessageButtonClicked = chatScreenViewModel::onSendChatMessageClicked,
                                onTypingMessageValueChange = chatScreenViewModel::onSendTextFieldValueChange,
                                onMessageClicked = chatScreenViewModel::onChatMessageClicked,
                                onMessageLongClicked = chatScreenViewModel::onChatMessageLongClicked,
                                onDeleteMessagesClicked = chatScreenViewModel::onDeleteChatMessagesConfirmed,
                            )
                        }
                    }
                }
            }
        }
    }
}




private const val TAG = "chatscreentag"
