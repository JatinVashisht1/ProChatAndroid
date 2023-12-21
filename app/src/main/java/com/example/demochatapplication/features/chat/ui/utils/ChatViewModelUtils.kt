package com.example.demochatapplication.features.chat.ui.utils

import com.example.demochatapplication.features.chat.domain.model.ChatScreenUiModel
import com.example.demochatapplication.features.chat.domain.model.MessageDeliveryState
import com.example.demochatapplication.features.chat.domain.model.ChatEventMessage

object ChatViewModelUtils {
    fun createChatModel(
        from: String,
        to: String,
        message: String,
        createdAt: Long,
        deliveryState: MessageDeliveryState,
        messageId: String
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

    fun createChatEventMessage(
        from: String,
        to: String,
        message: String,
        createdAt: Long,
        deliveryState: MessageDeliveryState,
        messageId: String
    ): ChatEventMessage {
        return ChatEventMessage(
            from = from,
            to = to,
            message = message,
            createdAt = createdAt,
            deliveryStatus = deliveryState.rawString,
            messageId = messageId
        )
    }
}