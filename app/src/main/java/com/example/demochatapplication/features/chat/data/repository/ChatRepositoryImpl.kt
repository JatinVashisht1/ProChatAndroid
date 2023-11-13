package com.example.demochatapplication.features.chat.data.repository

import android.accounts.NetworkErrorException
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.core.remote.dto.GetChatMessagesBetween2UsersDto
import com.example.demochatapplication.features.chat.data.database.ChatDatabase
import com.example.demochatapplication.features.chat.data.database.entity.ChatDbEntity
import com.example.demochatapplication.features.chat.domain.model.ChatModel
import com.example.demochatapplication.features.chat.domain.model.MessageDeliveryState
import com.example.demochatapplication.features.chat.domain.repository.ChatRepository
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Jatin Vashisht on 27-10-2023.
 */

class ChatRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val userSettingsRepository: UserSettingsRepository,
    chatDatabase: ChatDatabase,
    private val chatDbEntityAndModelMapper: Mapper<ChatDbEntity, ChatModel>,
    private val messageDeliveryStateAndStringMapper: Mapper<MessageDeliveryState, String>,
) : ChatRepository {
    private val chatDao = chatDatabase.chatDao
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

    override suspend fun doesMessageExist(messageId: String): Int = chatDao.doesMessageExist(messageId = messageId)

    override suspend fun getChatBetween2Users(
        currentUsername: String,
        anotherUsername: String,
        shouldLoadFromNetwork: Boolean
    ): Flow<List<ChatModel>> = flow {
        val credentials = userSettingsRepository.getFirstEntry()
        val authorizationHeader = credentials.token
        val chatMessagesCount =
            chatDao.getChatMessagesCount(username1 = currentUsername, username2 = anotherUsername)

        if (shouldLoadFromNetwork || chatMessagesCount == 0) {
            val chatEntityList = fetchChatMessagesFromNetwork(
                anotherUsername,
                authorizationHeader
            )

            saveChatMessagesToDatabase(
                currentUsername,
                anotherUsername,
                chatEntityList
            )
        }

        val chatDbEntityList =
            chatDao.getChatBetween2Users(username1 = currentUsername, username2 = anotherUsername)
        val chatModelList = mapDbEntitiesToChatModels(chatDbEntityList)

        emit(chatModelList)
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