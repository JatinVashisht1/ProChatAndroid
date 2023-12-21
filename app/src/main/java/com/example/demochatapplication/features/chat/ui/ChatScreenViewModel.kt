package com.example.demochatapplication.features.chat.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.demochatapplication.core.Constants
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.navigation.NavArgsKeys
import com.example.demochatapplication.features.chat.domain.model.ChatEventMessage
import com.example.demochatapplication.features.chat.domain.model.ChatScreenUiModel
import com.example.demochatapplication.features.chat.domain.model.MessageDeliveryState
import com.example.demochatapplication.features.chat.domain.model.UpdateAllMessageDeliveryStatusBetween2UsersModel
import com.example.demochatapplication.features.chat.domain.model.UpdateMessageDeliveryStateModel
import com.example.demochatapplication.features.chat.domain.repository.ChatRepository
import com.example.demochatapplication.features.chat.ui.uistate.ChatScreenState
import com.example.demochatapplication.features.chat.ui.utils.ChatViewModelUtils.createChatEventMessage
import com.example.demochatapplication.features.chat.ui.utils.ChatViewModelUtils.createChatModel
import com.example.demochatapplication.features.chat.ui.utils.jsonArrayToList
import com.example.demochatapplication.features.shared.socket.SocketEvents
import com.example.demochatapplication.features.shared.socket.SocketManager
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONArray
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@HiltViewModel()
class ChatScreenViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
    private val savedStateHandle: SavedStateHandle,
    private val chatRepository: ChatRepository,
    private val deliveryStateAndStringMapper: Mapper<MessageDeliveryState, String>,
) : ViewModel() {
    private var job: Job? = null

    private val _chatScreenState = MutableStateFlow<ChatScreenState>(ChatScreenState.Loading)
    val chatScreenState: StateFlow<ChatScreenState> = _chatScreenState

    private val _textMessagesListState: MutableStateFlow<PagingData<ChatScreenUiModel>> =
        MutableStateFlow(PagingData.empty())
    val textMessageListState get() = _textMessagesListState

    val chatScreenUiEvents = Channel<ChatScreenUiEvents>()

    private val onChat = Emitter.Listener { chatEventData ->
        viewModelScope.launch {
            handleChatEvent(chatEventData)
        }
    }

    private val onUpdateMessageDeliveryState = Emitter.Listener { updateMessageDeliveryState ->
        val updatedMessageStateString = updateMessageDeliveryState[0].toString()


        val (from, to, messageId, updatedStatus) = Json.decodeFromString<UpdateMessageDeliveryStateModel>(
            updatedMessageStateString
        )
        val messageDeliveryState = deliveryStateAndStringMapper.mapBtoA(updatedStatus)

        viewModelScope.launch {
            chatRepository.updateChatMessageDeliveryStatus(
                messageId = messageId,
                messageDeliveryState = messageDeliveryState
            )
        }
    }

    private val onUpdateAllMessageDeliveryState = Emitter.Listener { data ->
        viewModelScope.launch {
            val dataString = data[0].toString()
            val updateAllMessageDeliveryState =
                Json.decodeFromString<UpdateAllMessageDeliveryStatusBetween2UsersModel>(dataString)
            chatRepository.updateChatMessageDeliveryStatusOfAllMessagesBetween2Users(
                updateAllMessageDeliveryStatusBetween2UsersModel = updateAllMessageDeliveryState
            )
        }
    }

    private val onDeleteChatMessageEvent = Emitter.Listener { data ->
        viewModelScope.launch {
            val messageIds = data[0] as JSONArray
            val anotherUser = data[1] as String
            val initiatedBy = data[2] as String
            val messageIdsList = jsonArrayToList(messageIds)

            chatRepository.deleteChatMessagesByMessageId(
                messageIds = messageIdsList,
                initiatedBy = initiatedBy
            )
        }
    }

    init {
        viewModelScope.launch {
            launch {
                fetchUserCredentials()
                setAnotherUsernameStateValue()
                loadChatMessages()
            }

            launch {
                SocketManager.mSocket?.on(SocketEvents.Chat.eventName, onChat)
                SocketManager.mSocket?.on(
                    SocketEvents.UpdateAllMessagesDeliveryStatusBetween2Users.eventName,
                    onUpdateAllMessageDeliveryState
                )
                SocketManager.mSocket?.on(
                    SocketEvents.UpdateMessageDeliveryStatus.eventName,
                    onUpdateMessageDeliveryState
                )
                SocketManager.mSocket?.on(
                    SocketEvents.DeleteChatMessages.eventName,
                    onDeleteChatMessageEvent
                )
            }

            launch {
                updateAllMessageDeliveryStatusBetween2Users()
            }
        }
    }

    private suspend fun loadChatMessages() {
        val currentScreenState = _chatScreenState.value
        Timber.tag(TAG).d("inside load chat messages and screen state is $currentScreenState")
        if (currentScreenState is ChatScreenState.Success) {
            val pagedChatModelList = chatRepository.getChatBetween2Users(
                currentUsername = currentScreenState.userSettings.username,
                anotherUsername = currentScreenState.anotherUsername,
                shouldLoadFromNetwork = true
            )
                .stateIn(viewModelScope)
                .cachedIn(viewModelScope)
                .collectLatest {
                    _textMessagesListState.value = it
                }
        }
    }

    private suspend fun setAnotherUsernameStateValue() {
        Timber.tag(TAG).d("username received:")
        val usernameFromArgs = savedStateHandle.get<String>(NavArgsKeys.ANOTHER_USERNAME)
        usernameFromArgs?.let { anotherUsername ->
            val currentScreenState = _chatScreenState.value
            if (currentScreenState is ChatScreenState.Success) {
                _chatScreenState.value = currentScreenState.copy(anotherUsername = anotherUsername)
            }
        }
    }

    private suspend fun fetchUserCredentials() {
        _chatScreenState.value = ChatScreenState.Success()
        val currentState = _chatScreenState.value
//        userSettingsRepository.userSettings.collectLatest { userSettings ->
//                if (currentState is ChatScreenState.Success) {
//                _chatScreenState.value = currentState.copy(userSettings = userSettings)
//            }
//        }
//            }
            val newUserSettings = userSettingsRepository.getFirstEntry()
            if (currentState is ChatScreenState.Success) {
                _chatScreenState.value = currentState.copy(userSettings = newUserSettings)
        }
    }

        fun onSendTextFieldValueChange(newValue: String) {
            val currentScreenState = _chatScreenState.value

            if (currentScreenState is ChatScreenState.Success) {
                _chatScreenState.value = currentScreenState.copy(
                    sendTextMessageState = currentScreenState.sendTextMessageState.copy(message = newValue)
                )
            }
        }

        private suspend fun sendUiEvents(uiEvent: ChatScreenUiEvents) {
            chatScreenUiEvents.send(uiEvent)
        }

        fun onSendChatMessageClicked() {
            val currentScreenState = _chatScreenState.value

            if (currentScreenState is ChatScreenState.Success) {
                val from = currentScreenState.userSettings.username
                val to = currentScreenState.anotherUsername
                val message = currentScreenState.sendTextMessageState.message.trim()
                val createdAt = System.currentTimeMillis()
                val deliveryState = MessageDeliveryState.Sent
                val messageId = UUID.randomUUID().toString()

                viewModelScope.launch {
                    val chatEventMessage =
                        createChatEventMessage(
                            from,
                            to,
                            message,
                            createdAt,
                            deliveryState,
                            messageId
                        )
                    val chatModel =
                        createChatModel(from, to, message, createdAt, deliveryState, messageId)

                    //            insertChatMessage(chatModel)
                    sendChatEventMessage(chatEventMessage)
                    sendUiEvents(ChatScreenUiEvents.NavigateToFirstElement)
                }
            }
        }

        private fun insertChatMessage(chatModel: ChatScreenUiModel.ChatModel) {
            viewModelScope.launch {
                chatRepository.insertChatMessage(chatMessage = chatModel)
            }
        }

        private fun sendChatEventMessage(chatEventMessage: ChatEventMessage) {
            viewModelScope.launch {
                val currentScreenState = _chatScreenState.value

                if (currentScreenState is ChatScreenState.Success) {
                    val chatEventMessageJson = Json.encodeToString(chatEventMessage)

                    if (SocketManager.mSocket == null) {
                        SocketManager.setSocket(
                            Constants.SERVER_URL,
                            currentScreenState.userSettings.token
                        )
                        SocketManager.establishConnection()
                    }

                    SocketManager.mSocket?.emit(SocketEvents.Chat.eventName, chatEventMessageJson)
                }
            }
        }

        private suspend fun updateAllMessageDeliveryStatusBetween2Users() {
            withContext(IO) {
                val currentScreenState = _chatScreenState.value

                if (currentScreenState is ChatScreenState.Success) {
                    val username = currentScreenState.userSettings.username
                    val anotherUsername = currentScreenState.anotherUsername
                    val updateAllMessagesModel = UpdateAllMessageDeliveryStatusBetween2UsersModel(
                        from = anotherUsername,
                        to = username,
                        deliveryStatus = MessageDeliveryState.Read.rawString,
                    )
                    val updateAllMessageModelJson = Json.encodeToString(updateAllMessagesModel)
                    SocketManager.mSocket?.emit(
                        SocketEvents.UpdateAllMessagesDeliveryStatusBetween2Users.eventName,
                        updateAllMessageModelJson
                    )
                }
            }
        }


        private suspend fun handleChatEvent(chatEventData: Array<Any>) {
            val data = chatEventData[0].toString()
            // Timber.tag(TAG).d("message is $data")

            val chatMessageModel = parseChatEventData(data)
            val (from, to, message, createdAt, deliveryStatus, messageId) = chatMessageModel

            val shouldStoreInDatabase = chatRepository.doesMessageExist(messageId = messageId) == 0

            if (shouldStoreInDatabase) {
                handleDatabaseStorage(from, to, message, createdAt, deliveryStatus, messageId)
            }

            sendUpdateMessageDeliveryStatus(message = chatMessageModel)
        }

        private fun parseChatEventData(data: String): ChatEventMessage {
            return Json.decodeFromString(data)
        }

        private suspend fun handleDatabaseStorage(
            from: String,
            to: String,
            message: String,
            createdAt: Long,
            deliveryStatus: String,
            messageId: String
        ) {
            val deliveryState = deliveryStateAndStringMapper.mapBtoA(deliveryStatus)
            val chatModel = createChatModel(from, to, message, createdAt, messageId, deliveryState)

            val messageFromCurrentUser = isMessageFromCurrentUser(to, from)
            val messageFromAnotherUser = isMessageFromAnotherUser(to, from)

            chatRepository.insertChatMessage(chatMessage = chatModel)

            if (messageFromAnotherUser) {
                handleUpdateMessageDeliveryState(from, messageId)
            }
        }

        private fun createChatModel(
            from: String,
            to: String,
            message: String,
            createdAt: Long,
            messageId: String,
            deliveryState: MessageDeliveryState
        ): ChatScreenUiModel.ChatModel {
            return ChatScreenUiModel.ChatModel(
                from = from,
                to = to,
                message = message,
                timeInMillis = createdAt,
                id = messageId,
                deliveryState = deliveryState,
                deletedByReceiver = false,
            )
        }

        private fun isMessageFromCurrentUser(to: String, from: String): Boolean {
            val currentScreenState = _chatScreenState.value
            if (currentScreenState is ChatScreenState.Success) {
                return to == currentScreenState.anotherUsername && from == currentScreenState.userSettings.username
            }
            return false
        }

        private fun isMessageFromAnotherUser(to: String, from: String): Boolean {
            val currentScreenState = _chatScreenState.value
            if (currentScreenState is ChatScreenState.Success) {
                return to == currentScreenState.userSettings.username && from == currentScreenState.anotherUsername
            }

            return false
        }

        private fun handleUpdateMessageDeliveryState(from: String, messageId: String) {
            val updateMessageDeliveryStateModel =
                createUpdateMessageDeliveryStateModel(from, messageId)
            val updateMessageDeliveryStateString =
                Json.encodeToString(updateMessageDeliveryStateModel)
            SocketManager.mSocket?.emit(
                SocketEvents.UpdateMessageDeliveryStatus.eventName,
                updateMessageDeliveryStateString
            )
        }

        private fun createUpdateMessageDeliveryStateModel(
            from: String,
            messageId: String
        ): UpdateMessageDeliveryStateModel {
            val currentScreenState = _chatScreenState.value

            if (currentScreenState is ChatScreenState.Success) {
                return UpdateMessageDeliveryStateModel(
                    from = currentScreenState.anotherUsername,
                    to = currentScreenState.userSettings.username,
                    messageId = messageId,
                    updatedStatus = MessageDeliveryState.Read.rawString,
                )
            }

            throw Throwable("screen state is not set to success")
        }

        private fun sendUpdateMessageDeliveryStatus(message: ChatEventMessage) {
            val currentScreenState = _chatScreenState.value

            if (currentScreenState is ChatScreenState.Success) {

                if (message.to == currentScreenState.userSettings.username) {
                    val updateMessageDeliveryStateModel = UpdateMessageDeliveryStateModel(
                        from = message.from,
                        to = message.to,
                        messageId = message.messageId,
                        updatedStatus = MessageDeliveryState.Read.rawString
                    )
                    SocketManager.mSocket?.emit(
                        SocketEvents.UpdateMessageDeliveryStatus.eventName,
                        Json.encodeToString(updateMessageDeliveryStateModel)
                    )
                }
            }
        }

        fun onDeleteChatMessagesConfirmed() {
            val currentScreenState = _chatScreenState.value

            if (currentScreenState is ChatScreenState.Success) {
                viewModelScope.launch {
                    val messageIds = currentScreenState.selectedMessages.toList()
                    val jsonMessageIds = Json.encodeToString(messageIds)
                    SocketManager.mSocket?.emit(
                        SocketEvents.DeleteChatMessages.eventName,
                        jsonMessageIds,
                        currentScreenState.anotherUsername,
                        currentScreenState.userSettings.username
                    )

                    chatRepository.deleteChatMessagesByMessageId(
                        messageIds = messageIds,
                        initiatedBy = currentScreenState.userSettings.username
                    )

                    _chatScreenState.value = currentScreenState.copy(selectedMessages = emptySet())
                }
            }
        }

        fun onChatMessageClicked(messageId: String) {
            val currentScreenState = _chatScreenState.value

            if (currentScreenState is ChatScreenState.Success && currentScreenState.isSelectionModeEnabled) {
                val messageAlreadySelected =
                    currentScreenState.selectedMessages.contains(messageId)
                val newSelectedSet = currentScreenState.selectedMessages.toMutableSet()


                if (messageAlreadySelected) {
                    newSelectedSet.remove(messageId)
                } else {
                    newSelectedSet.add(messageId)
                }
                Timber.tag(TAG).d("on message clicked invoked for $messageId, $messageAlreadySelected, ${newSelectedSet.size}")
                _chatScreenState.value = currentScreenState.copy(selectedMessages = newSelectedSet.toSet())
            }
        }

        fun onChatMessageLongClicked(messageId: String) {
            val currentScreenState = _chatScreenState.value

            if (currentScreenState is ChatScreenState.Success && !currentScreenState.isSelectionModeEnabled) {
                _chatScreenState.value = currentScreenState.copy(
                    isSelectionModeEnabled = true,
                    selectedMessages = setOf(messageId),
                )
            }
        }

        fun onBackButtonPressed() {
            viewModelScope.launch {
                val currentScreenState = _chatScreenState.value
                if (currentScreenState is ChatScreenState.Success && currentScreenState.isSelectionModeEnabled) {
                    _chatScreenState.value = currentScreenState.copy(
                        isSelectionModeEnabled = false,
                        selectedMessages = setOf()
                    )
                } else {
                    sendUiEvents(ChatScreenUiEvents.NavigateUp)
                }
            }
        }

        override fun onCleared() {
            super.onCleared()
            job?.cancel()
        }

        companion object {
            const val TAG = "chatscreenviewmodel"
        }
    }

    sealed class ChatScreenUiEvents {
        data object NavigateToFirstElement : ChatScreenUiEvents()
        data object NavigateUp : ChatScreenUiEvents()
    }
