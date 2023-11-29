package com.example.demochatapplication.features.chat.data.repository

import android.accounts.NetworkErrorException
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.core.remote.dto.GetChatMessagesBetween2UsersDto
import com.example.demochatapplication.features.chat.data.database.ChatDatabase
import com.example.demochatapplication.features.chat.data.database.entity.ChatDbEntity
import com.example.demochatapplication.features.chat.data.database.entity.UpdateAllMessageDeliveryStatusBetween2UsersEntity
import com.example.demochatapplication.features.chat.data.database.entity.UpdateMessageDeliveryStatusDbEntity
import com.example.demochatapplication.features.chat.data.paging.ChatMessagesRemoteMediator
import com.example.demochatapplication.features.chat.domain.model.ChatScreenUiModel
import com.example.demochatapplication.features.chat.domain.model.MessageDeliveryState
import com.example.demochatapplication.features.chat.domain.model.UpdateAllMessageDeliveryStatusBetween2UsersModel
import com.example.demochatapplication.features.chat.domain.repository.ChatRepository
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Jatin Vashisht on 27-10-2023.
 */

@OptIn(ExperimentalPagingApi::class)
class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val userSettingsRepository: UserSettingsRepository,
    private val chatDatabase: ChatDatabase,
    private val chatDbEntityAndModelMapper: Mapper<ChatDbEntity, ChatScreenUiModel.ChatModel>,
    private val messageDeliveryStateAndStringMapper: Mapper<MessageDeliveryState, String>,
    private val chatMessageDtoAndDbEntityMapper: Mapper<GetChatMessagesBetween2UsersDto, List<ChatDbEntity>>,
) : ChatRepository {
    private val chatDao = chatDatabase.chatDao
    private lateinit var authorizationHeader: String
    private var hasFoundUnreadMessage = false

    init {
        CoroutineScope(IO).launch {
            authorizationHeader = userSettingsRepository.getFirstEntry().token
        }
    }

    private suspend fun fetchChatMessagesFromNetwork(
        anotherUsername: String,
        authorizationHeader: String
    ): List<ChatDbEntity> {
        val getChatMessagesResponse = chatApi.getChatMessagesBetween2Users(
            authorizationHeader = authorizationHeader,
            anotherUsername = anotherUsername
        )

        Timber.tag(TAG).d("result from network: ${getChatMessagesResponse.isSuccessful}")

        if (!getChatMessagesResponse.isSuccessful) {
            throw NetworkErrorException("unable to fetch chat")
        }

        val responseBody = getChatMessagesResponse.body() ?: GetChatMessagesBetween2UsersDto()

        return responseBody.messages.map { message ->
            val deliveryState = messageDeliveryStateAndStringMapper.mapBtoA(message.deliveryStatus)
            ChatDbEntity(
                from = message.senderUsername,
                to = message.receiverUsername,
                message = message.message,
                timeStamp = (message.createdTime.toLong() / 1000),
                primaryKey = message.messageId,
                deliveryStatus = deliveryState
            )
        }
    }

    private suspend fun saveChatMessagesToDatabase(
        currentUsername: String,
        anotherUsername: String,
        chatEntityList: List<ChatDbEntity>
    ) {
        withContext(IO) {
            chatDao.insertChatMessage(message = chatEntityList)
        }
    }

    private fun mapDbEntitiesToChatModels(chatDbEntityList: List<ChatDbEntity>): List<ChatScreenUiModel.ChatModel> {
        return chatDbEntityList.map {
            chatDbEntityAndModelMapper.mapAtoB(it)
        }
    }

    override suspend fun doesMessageExist(messageId: String): Int = withContext(IO) {
        chatDao.doesMessageExist(messageId = messageId)
    }

    override fun getChatBetween2Users(
        currentUsername: String,
        anotherUsername: String,
        shouldLoadFromNetwork: Boolean
    ): Flow<PagingData<ChatScreenUiModel>> {
        Timber.tag(TAG)
            .d("credentials received: $currentUsername, $anotherUsername, $authorizationHeader")
        val pagingData = Pager<Int, ChatDbEntity>(
            config = PagingConfig(
                pageSize = 50,
                prefetchDistance = 20,
            ),
            remoteMediator = ChatMessagesRemoteMediator(
                chatMessageDatabase = chatDatabase,
                chatApi = chatApi,
                chatMessageDtoAndDbEntityMapper = chatMessageDtoAndDbEntityMapper,
                currentUser = currentUsername,
                anotherUser = anotherUsername,
                authorizationHeader = authorizationHeader
            )
        ) {
            chatDao.getChatBetween2Users(username1 = currentUsername, username2 = anotherUsername)
        }
            .flow
            .map { pagingDataDbEntity: PagingData<ChatDbEntity> ->
                pagingDataDbEntity.map { chatDbEntity: ChatDbEntity ->
                    chatDbEntityAndModelMapper.mapAtoB(chatDbEntity)
                }
                    .insertSeparators { before: ChatScreenUiModel.ChatModel?, _: ChatScreenUiModel.ChatModel? ->
                        when {
                            (((before?.deliveryState == MessageDeliveryState.Sent || before?.deliveryState == MessageDeliveryState.Received) && (!hasFoundUnreadMessage))) -> {
                                hasFoundUnreadMessage = true
                                ChatScreenUiModel.UnreadMessagesModel()
                            }

                            else -> null
                        }
                    }
            }



        return pagingData
    }

    override suspend fun insertChatMessage(chatMessage: ChatScreenUiModel.ChatModel) {
        withContext(IO) {
            val chatDbEntity = chatDbEntityAndModelMapper.mapBtoA(chatMessage)

            chatDao.insertChatMessage(message = chatDbEntity)
        }
    }

    override suspend fun insertChatMessage(chatMessage: List<ChatScreenUiModel.ChatModel>) {
        withContext(IO) {
            val chatDbEntityList = chatMessage.map { chatModel ->
                chatDbEntityAndModelMapper.mapBtoA(chatModel)
            }

            chatDao.insertChatMessage(chatDbEntityList)
        }
    }

    override suspend fun updateChatMessageDeliveryStatus(
        messageId: String,
        messageDeliveryState: MessageDeliveryState
    ) {
        withContext(IO) {
            chatDao.updateChatMessageDeliveryStatus(
                UpdateMessageDeliveryStatusDbEntity(
                    primaryKey = messageId,
                    deliveryStatus = messageDeliveryState
                )
            )
        }
    }

    override suspend fun updateChatMessageDeliveryStatusOfAllMessagesBetween2Users(
        updateAllMessageDeliveryStatusBetween2UsersModel: UpdateAllMessageDeliveryStatusBetween2UsersModel
    ) {
        withContext(IO) {
            val (from, to, messageDeliveryStateString) = updateAllMessageDeliveryStatusBetween2UsersModel
            val messageDeliveryState =
                messageDeliveryStateAndStringMapper.mapBtoA(messageDeliveryStateString)

            val updateAllMessageDeliveryStatusBetween2UsersEntity =
                UpdateAllMessageDeliveryStatusBetween2UsersEntity(
                    from = from,
                    to = to,
                    messageDeliveryState = messageDeliveryState
                )
            chatDao.updateMessageDeliveryStatusBetween2Users(
                from = from,
                to = to,
                messageDeliveryState = messageDeliveryState
            )
        }
    }

    companion object {
        const val TAG = "chatrepositoryimplementation"
    }
}