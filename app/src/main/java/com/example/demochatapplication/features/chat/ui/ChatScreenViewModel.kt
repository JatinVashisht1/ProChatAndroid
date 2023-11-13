package com.example.demochatapplication.features.chat.ui

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demochatapplication.core.Constants
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.features.chat.domain.model.ChatModel
import com.example.demochatapplication.features.chat.domain.model.MessageDeliveryState
import com.example.demochatapplication.features.chat.domain.repository.ChatRepository
import com.example.demochatapplication.features.chat.ui.uistate.ChatScreenState
import com.example.demochatapplication.features.chat.ui.uistate.SendMessageTextFieldState
import com.example.demochatapplication.features.shared.navigation.NavArgsKeys
import com.example.demochatapplication.features.shared.socket.ChatEventMessage
import com.example.demochatapplication.features.shared.socket.SocketEvents
import com.example.demochatapplication.features.shared.socket.SocketManager
import com.example.demochatapplication.features.shared.usersettings.UserSettings
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel()
class ChatScreenViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
    private val savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val deliveryStateAndStringMapper: Mapper<MessageDeliveryState, String>,
) : ViewModel() {
    private val _anotherUsernameState = MutableStateFlow<String>("")
    val anotherUsernameState: StateFlow<String> get() = _anotherUsernameState.asStateFlow()

    private val _userSettingsStateFlow = MutableStateFlow<UserSettings>(UserSettings())
    val userSettingsStateFlow: StateFlow<UserSettings> = _userSettingsStateFlow.asStateFlow()

    private val _chatScreenState = MutableStateFlow<ChatScreenState>(ChatScreenState.Loading)
    val chatScreenState: StateFlow<ChatScreenState> = _chatScreenState

    private val _sendMessageTextFieldState =
        MutableStateFlow<SendMessageTextFieldState>(SendMessageTextFieldState())
    val sendMessageTextFieldState: StateFlow<SendMessageTextFieldState> get() = _sendMessageTextFieldState

    private val _textMessagesListState = mutableStateListOf<ChatModel>()
    val textMessageListState get() = _textMessagesListState.toList()

    private val onChat = Emitter.Listener {
        viewModelScope.launch {
            val data = it[0].toString()
            val (from, to, message, createdAt, deliveryStatus, messageId) = Json.decodeFromString<ChatEventMessage>(
                data
            )

            val shouldStoreInDatabase = chatRepository.doesMessageExist(messageId = messageId) == 0

            if (shouldStoreInDatabase) {
                val deliveryState = deliveryStateAndStringMapper.mapBtoA(deliveryStatus)
                val chatModel = ChatModel(
                    from = from,
                    to = to,
                    message = message,
                    time = createdAt,
                    id = messageId,
                    deliveryState = deliveryState,
                )

                val messageFromCurrentUser =
                    (to == _anotherUsernameState.value && from == _userSettingsStateFlow.value.username)
                val messageFromAnotherUser =
                    (to == _userSettingsStateFlow.value.username && from == _anotherUsernameState.value)

                if (messageFromAnotherUser || messageFromCurrentUser) {
                    _textMessagesListState.add(chatModel)
                }

                chatRepository.insertChatMessage(chatMessage = chatModel)
            }
        }

    }

    init {
        viewModelScope.launch {
            launch { fetchUserCredentials() }
            val job1 = launch {
                setAnotherUsernameStateValue()
            }
            job1.join()
            launch {
                loadChatMessages()
            }
        }

        viewModelScope.launch {
            SocketManager.mSocket?.on("chat", onChat)
        }
    }

    private suspend fun loadChatMessages() {
//        Timber.tag(TAG).d("entered load messages function")
        chatRepository.getChatBetween2Users(
            currentUsername = _userSettingsStateFlow.value.username,
            anotherUsername = _anotherUsernameState.value,
            shouldLoadFromNetwork = true
        ).collectLatest {
            it.forEach { chatModel ->
                _textMessagesListState.add(chatModel)
            }

            Timber.tag(TAG).d("fetched messages: $it")
        }
    }

    private suspend fun setAnotherUsernameStateValue() {
        Timber.tag(TAG).d("username received:")
        val usernameFromArgs = savedStateHandle.get<String>(NavArgsKeys.ANOTHER_USERNAME)
        usernameFromArgs?.let {
            _anotherUsernameState.value = it
        }
    }

    private suspend fun fetchUserCredentials() {
        userSettingsRepository.userSettings.collectLatest { userSettings ->
            _userSettingsStateFlow.value = userSettings
            _chatScreenState.value = ChatScreenState.Success()
        }
    }

    fun onSendTextFieldValueChange(newValue: String) {
//        Timber.tag(TAG).d("chat screen view model $newValue")
        _sendMessageTextFieldState.value = _sendMessageTextFieldState.value.copy(message = newValue)
    }

    fun onSendChatMessageClicked() {
        val from = _userSettingsStateFlow.value.username
        val to = _anotherUsernameState.value
        val message = _sendMessageTextFieldState.value.message
        val createdAt = Instant.now().epochSecond
        val deliveryState = MessageDeliveryState.Sent
        val messageId = UUID.randomUUID().toString()

        viewModelScope.launch {

            coroutineScope {
                val chatEventMessage = ChatEventMessage(
                    from = from,
                    to = to,
                    message = message,
                    createdAt = createdAt,
                    deliveryStatus = deliveryState.rawString,
                    messageId = messageId,
                )

                val chatModel = ChatModel(
                    from = from,
                    to = to,
                    message = message,
                    time = createdAt,
                    id = messageId,
                    deliveryState = deliveryState,
                )


                launch {
                    chatRepository.insertChatMessage(chatMessage = chatModel)
                }

                launch {
                    val chatEventMessageJson = Json.encodeToString(chatEventMessage)

                    if (SocketManager.mSocket == null) {
                        SocketManager.setSocket(
                            Constants.SERVER_URL, _userSettingsStateFlow.value.token
                        )
                        SocketManager.establishConnection()
                    }


                    SocketManager.mSocket?.emit(
                        SocketEvents.Chat.eventName, chatEventMessageJson
                    )
                }

                withContext(Main) {
                    _textMessagesListState.add(chatModel)
                }
            }
        }
    }

    companion object {
        const val TAG = "chatscreenviewmodel"
    }
}
