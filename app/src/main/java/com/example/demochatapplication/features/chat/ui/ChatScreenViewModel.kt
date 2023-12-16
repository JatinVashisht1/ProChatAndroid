package com.example.demochatapplication.features.chat.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.demochatapplication.core.Constants
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.features.chat.domain.model.ChatScreenUiModel
import com.example.demochatapplication.features.chat.domain.model.MessageDeliveryState
import com.example.demochatapplication.features.chat.domain.repository.ChatRepository
import com.example.demochatapplication.features.chat.ui.uistate.ChatScreenState
import com.example.demochatapplication.features.chat.ui.uistate.SendMessageTextFieldState
import com.example.demochatapplication.features.chat.ui.utils.ChatViewModelUtils.createChatEventMessage
import com.example.demochatapplication.features.chat.ui.utils.ChatViewModelUtils.createChatModel
import com.example.demochatapplication.core.navigation.NavArgsKeys
import com.example.demochatapplication.features.chat.domain.model.ChatEventMessage
import com.example.demochatapplication.features.shared.socket.SocketEvents
import com.example.demochatapplication.features.shared.socket.SocketManager
import com.example.demochatapplication.features.chat.domain.model.UpdateAllMessageDeliveryStatusBetween2UsersModel
import com.example.demochatapplication.features.chat.domain.model.UpdateMessageDeliveryStateModel
import com.example.demochatapplication.features.shared.usersettings.UserSettings
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
    private val _anotherUsernameState = MutableStateFlow<String>("")
    val anotherUsernameState: StateFlow<String> get() = _anotherUsernameState.asStateFlow()

    private val _userSettingsStateFlow = MutableStateFlow<UserSettings>(UserSettings())
    val userSettingsStateFlow: StateFlow<UserSettings> = _userSettingsStateFlow.asStateFlow()

    private val _chatScreenState = MutableStateFlow<ChatScreenState>(ChatScreenState.Loading)
    val chatScreenState: StateFlow<ChatScreenState> = _chatScreenState

    private val _sendMessageTextFieldState =
        MutableStateFlow<SendMessageTextFieldState>(SendMessageTextFieldState())
    val sendMessageTextFieldState: StateFlow<SendMessageTextFieldState> get() = _sendMessageTextFieldState

    private lateinit var _textMessagesListState: Flow<PagingData<ChatScreenUiModel>>
    val textMessageListState get() = _textMessagesListState

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

    private val onUpdateAllMessageDeliveryState = Emitter.Listener {data ->
        viewModelScope.launch {
            val dataString = data[0].toString()
            val updateAllMessageDeliveryState = Json.decodeFromString<UpdateAllMessageDeliveryStatusBetween2UsersModel>(dataString)
            chatRepository.updateChatMessageDeliveryStatusOfAllMessagesBetween2Users(updateAllMessageDeliveryStatusBetween2UsersModel = updateAllMessageDeliveryState)
        }
    }

    init {
        viewModelScope.launch {
            launch { fetchUserCredentials() }

            job?.cancel()
            job = launch {
                setAnotherUsernameStateValue()
            }

            job?.join()

            launch {
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
            }

            launch {
                updateAllMessageDeliveryStatusBetween2Users()
            }
        }
    }

    private suspend fun loadChatMessages() {
//        Timber.tag(TAG).d("entered load messages function")
        val pagedChatModelList = chatRepository.getChatBetween2Users(
            currentUsername = _userSettingsStateFlow.value.username,
            anotherUsername = _anotherUsernameState.value,
            shouldLoadFromNetwork = true
        )

        _textMessagesListState = pagedChatModelList

//        _textMessagesListState = pagedChatModelList
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
        val createdAt = System.currentTimeMillis()
        val deliveryState = MessageDeliveryState.Sent
        val messageId = UUID.randomUUID().toString()

        viewModelScope.launch {
            val chatEventMessage =
                createChatEventMessage(from, to, message, createdAt, deliveryState, messageId)
            val chatModel = createChatModel(from, to, message, createdAt, deliveryState, messageId)

//            insertChatMessage(chatModel)
            sendChatEventMessage(chatEventMessage)
        }
    }

    private fun insertChatMessage(chatModel: ChatScreenUiModel.ChatModel) {
        viewModelScope.launch {
            chatRepository.insertChatMessage(chatMessage = chatModel)
        }
    }

    private fun sendChatEventMessage(chatEventMessage: ChatEventMessage) {
        viewModelScope.launch {
            val chatEventMessageJson = Json.encodeToString(chatEventMessage)

            if (SocketManager.mSocket == null) {
                SocketManager.setSocket(Constants.SERVER_URL, _userSettingsStateFlow.value.token)
                SocketManager.establishConnection()
            }

            SocketManager.mSocket?.emit(SocketEvents.Chat.eventName, chatEventMessageJson)
        }
    }

    private suspend fun updateAllMessageDeliveryStatusBetween2Users() {
        withContext(IO) {
            val username = _userSettingsStateFlow.value.username
            val anotherUsername = _anotherUsernameState.value
            val updateAllMessagesModel = UpdateAllMessageDeliveryStatusBetween2UsersModel(
                from = anotherUsername,
                to = username,
                deliveryStatus = MessageDeliveryState.Read.rawString,
            )
            val updateAllMessageModelJson = Json.encodeToString(updateAllMessagesModel)
            SocketManager.mSocket?.emit(SocketEvents.UpdateAllMessagesDeliveryStatusBetween2Users.eventName, updateAllMessageModelJson)
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
        )
    }

    private fun isMessageFromCurrentUser(to: String, from: String): Boolean {
        return to == _anotherUsernameState.value && from == _userSettingsStateFlow.value.username
    }

    private fun isMessageFromAnotherUser(to: String, from: String): Boolean {
        return to == _userSettingsStateFlow.value.username && from == _anotherUsernameState.value
    }

    private fun handleUpdateMessageDeliveryState(from: String, messageId: String) {
        val updateMessageDeliveryStateModel = createUpdateMessageDeliveryStateModel(from, messageId)
        val updateMessageDeliveryStateString = Json.encodeToString(updateMessageDeliveryStateModel)
        SocketManager.mSocket?.emit(SocketEvents.UpdateMessageDeliveryStatus.eventName, updateMessageDeliveryStateString)
    }

    private fun createUpdateMessageDeliveryStateModel(from: String, messageId: String): UpdateMessageDeliveryStateModel {
        return UpdateMessageDeliveryStateModel(
            from = _anotherUsernameState.value,
            to = _userSettingsStateFlow.value.username,
            messageId = messageId,
            updatedStatus = MessageDeliveryState.Read.rawString,
        )
    }

    private fun sendUpdateMessageDeliveryStatus (message: ChatEventMessage) {
        if (message.to == _userSettingsStateFlow.value.username) {
            val updateMessageDeliveryStateModel = UpdateMessageDeliveryStateModel(from = message.from, to = message.to, messageId = message.messageId, updatedStatus = MessageDeliveryState.Read.rawString)
            SocketManager.mSocket?.emit(SocketEvents.UpdateMessageDeliveryStatus.eventName, Json.encodeToString(updateMessageDeliveryStateModel))
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
