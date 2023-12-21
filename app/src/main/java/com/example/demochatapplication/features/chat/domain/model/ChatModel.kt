package com.example.demochatapplication.features.chat.domain.model

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

sealed class ChatScreenUiModel {
    data class ChatModel constructor(
        val from: String = "",
        val to: String = "",
        val message: String = "",
        val timeInMillis: Long = System.currentTimeMillis(),
        val id: String = UUID.randomUUID().toString(),
        val deliveryState: MessageDeliveryState = MessageDeliveryState.Sent,
        val deletedByReceiver: Boolean,
    ): ChatScreenUiModel()

    data class UnreadMessagesModel constructor(
        val data: String = "unread messages"
    ): ChatScreenUiModel()
}
