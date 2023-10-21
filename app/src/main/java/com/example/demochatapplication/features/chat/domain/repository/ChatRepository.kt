package com.example.demochatapplication.features.chat.domain.repository

import com.example.demochatapplication.core.Resource
import com.example.demochatapplication.features.chat.domain.model.ChatModel
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getChatBetween2Users(from: String, to: String): Flow<Resource<List<ChatModel>>>
    suspend fun deleteMessage(messageId: String)
}