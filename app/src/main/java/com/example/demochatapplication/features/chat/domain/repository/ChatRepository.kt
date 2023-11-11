package com.example.demochatapplication.features.chat.domain.repository

import com.example.demochatapplication.core.Resource
import com.example.demochatapplication.features.chat.domain.model.ChatModel
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getChatBetween2Users(currentUsername: String, anotherUsername: String, shouldLoadFromNetwork: Boolean): Flow<List<ChatModel>>

    suspend fun insertChatMessage(chatMessage: ChatModel)

    suspend fun insertChatMessage(chatMessage: List<ChatModel>)
}