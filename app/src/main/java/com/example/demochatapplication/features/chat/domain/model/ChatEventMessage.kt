package com.example.demochatapplication.features.chat.domain.model

import com.example.demochatapplication.features.chat.domain.model.MessageDeliveryState
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Created by Jatin Vashisht on 06-11-2023.
 */

@Serializable()
data class ChatEventMessage(
    val from: String,
    val to: String,
    val message: String,
    val createdAt: Long,
    val deliveryStatus: String,
    val messageId: String,
)
