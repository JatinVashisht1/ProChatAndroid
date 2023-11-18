package com.example.demochatapplication.features.chat.domain.repository

import androidx.paging.PagingData
import com.example.demochatapplication.core.Resource
import com.example.demochatapplication.features.chat.domain.model.ChatModel
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatBetween2Users(currentUsername: String, anotherUsername: String, shouldLoadFromNetwork: Boolean): Flow<PagingData<ChatModel>>

    suspend fun insertChatMessage(chatMessage: ChatModel)

    suspend fun insertChatMessage(chatMessage: List<ChatModel>)

    suspend fun doesMessageExist(messageId: String): Int
}