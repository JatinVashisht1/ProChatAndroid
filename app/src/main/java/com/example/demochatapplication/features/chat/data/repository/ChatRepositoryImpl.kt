package com.example.demochatapplication.features.chat.data.repository

import android.accounts.NetworkErrorException
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.RemoteMediator
import androidx.paging.map
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.core.remote.dto.GetChatMessagesBetween2UsersDto
import com.example.demochatapplication.features.chat.data.database.ChatDatabase
import com.example.demochatapplication.features.chat.data.database.entity.ChatDbEntity
import com.example.demochatapplication.features.chat.data.paging.ChatMessagesRemoteMediator
import com.example.demochatapplication.features.chat.domain.model.ChatModel
import com.example.demochatapplication.features.chat.domain.model.MessageDeliveryState
import com.example.demochatapplication.features.chat.domain.repository.ChatRepository
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
    private val chatDbEntityAndModelMapper: Mapper<ChatDbEntity, ChatModel>,
    private val messageDeliveryStateAndStringMapper: Mapper<MessageDeliveryState, String>,
    private val chatMessageDtoAndDbEntityMapper: Mapper<GetChatMessagesBetween2UsersDto, List<ChatDbEntity>>,
) : ChatRepository {
    private val chatDao = chatDatabase.chatDao
    private lateinit var authorizationHeader: String

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
        /*
        chatDao.deleteAndInsertChats(
            username1 = currentUsername,
            username2 = anotherUsername,
            chatDbEntity = chatEntityList
        )
         */
        withContext(IO) {
            chatDao.insertChatMessage(message = chatEntityList)
        }
    }

    private fun mapDbEntitiesToChatModels(chatDbEntityList: List<ChatDbEntity>): List<ChatModel> {
        return chatDbEntityList.map {
            chatDbEntityAndModelMapper.mapAtoB(it)
        }
    }

    override suspend fun doesMessageExist(messageId: String): Int =
        chatDao.doesMessageExist(messageId = messageId)

    override fun getChatBetween2Users(
        currentUsername: String,
        anotherUsername: String,
        shouldLoadFromNetwork: Boolean
    ): Flow<PagingData<ChatModel>> {
        Timber.tag(TAG).d("credentials received: $currentUsername, $anotherUsername, $authorizationHeader")
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
            }

        return pagingData
    }

    override suspend fun insertChatMessage(chatMessage: ChatModel) {
        withContext(IO) {
            val chatDbEntity = chatDbEntityAndModelMapper.mapBtoA(chatMessage)

            chatDao.insertChatMessage(message = chatDbEntity)
        }
    }

    override suspend fun insertChatMessage(chatMessage: List<ChatModel>) {
        withContext(IO) {
            val chatDbEntityList = chatMessage.map { chatModel ->
                chatDbEntityAndModelMapper.mapBtoA(chatModel)
            }

            chatDao.insertChatMessage(chatDbEntityList)
        }
    }

    companion object {
        const val TAG = "chatrepositoryimplementation"
    }
}