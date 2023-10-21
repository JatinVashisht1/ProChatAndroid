package com.example.demochatapplication.features.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.demochatapplication.features.chat.domain.model.ChatModel
import com.example.demochatapplication.features.chat.ui.components.ChatMessageCard
import com.example.demochatapplication.features.chat.ui.components.SendMessageTextField
import com.example.demochatapplication.features.chat.ui.uistate.ChatScreenState
import com.example.demochatapplication.features.chat.ui.uistate.SendMessageTextFieldState
import com.example.demochatapplication.features.chat.ui.utils.CornerRoundnessDpValues
import com.example.demochatapplication.features.login.ui.utils.PaddingValues

@Composable
fun ChatScreen(
    chatScreenViewModel: ChatScreenViewModel = hiltViewModel(),
) {
    val userCredentials = chatScreenViewModel.userSettingsStateFlow.collectAsState().value
    val sendMessageTextFieldState by chatScreenViewModel.sendMessageTextFieldState.collectAsState()
    val textMessages = chatScreenViewModel.textMessageListState

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
                    CircularProgressIndicator()
                }

                is ChatScreenState.Success -> {
                    ChatScreenContent(
                        textFieldState = sendMessageTextFieldState,
                        onTypingMessageValueChange = chatScreenViewModel::onSendTextFieldValueChange,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(PaddingValues.MEDIUM),
                        onButtonClicked = chatScreenViewModel::addRandomItem,
                        textMessages = textMessages,
                        username = userCredentials.username,
                    )
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
    onButtonClicked: () -> Unit,
    textMessages: List<ChatModel>,
) {
    Column(modifier = modifier) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight(0.7f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            items(textMessages) { chatModel ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    ChatMessageCard(
                        chatModel = chatModel,
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .wrapContentHeight()
                            .align(if (username == chatModel.from) Alignment.CenterEnd else Alignment.CenterStart),
                        cornerRoundnessDpValues = CornerRoundnessDpValues(
                            bottomStart = if (username == chatModel.from) 25.dp else 0.dp,
                            bottomEnd = if (username == chatModel.from) 0.dp else 25.dp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(PaddingValues.MEDIUM))
            }
        }

        Button(onClick = onButtonClicked) {
            Text(text = "Add Item")
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SendMessageTextField(
                textFieldState = textFieldState,
                onTypingMessageValueChange = onTypingMessageValueChange,
            )
        }
    }
}

private const val TAG = "chatscreentag"