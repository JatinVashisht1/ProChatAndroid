package com.example.demochatapplication.features.chat.domain.repository

import androidx.paging.PagingData
import com.example.demochatapplication.features.chat.domain.model.ChatScreenUiModel
import com.example.demochatapplication.features.chat.domain.model.DeleteChatMessageBodyModel
import com.example.demochatapplication.features.chat.domain.model.DeleteChatMessageResponseModel
import com.example.demochatapplication.features.chat.domain.model.MessageDeliveryState
import com.example.demochatapplication.features.chat.domain.model.UpdateAllMessageDeliveryStatusBetween2UsersModel
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatBetween2Users(
        currentUsername: String,
        anotherUsername: String,
        shouldLoadFromNetwork: Boolean
    ): Flow<PagingData<ChatScreenUiModel>>

    suspend fun insertChatMessage(chatMessage: ChatScreenUiModel.ChatModel)

    suspend fun insertChatMessage(chatMessage: List<ChatScreenUiModel.ChatModel>)

    suspend fun doesMessageExist(messageId: String): Int

    suspend fun updateChatMessageDeliveryStatus(
        messageId: String,
        messageDeliveryState: MessageDeliveryState
    )

    suspend fun updateChatMessageDeliveryStatusOfAllMessagesBetween2Users(
        updateAllMessageDeliveryStatusBetween2UsersModel: UpdateAllMessageDeliveryStatusBetween2UsersModel
    )

    suspend fun deleteChatMessageFromNetwork(
        deleteChatMessageBodyModel: DeleteChatMessageBodyModel
    ): DeleteChatMessageResponseModel

    suspend fun deleteChatMessagesByMessageId(
        messageIds: List<String>,
        initiatedBy: String
    )
}