package com.example.demochatapplication.features.chat.domain.model

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID

data class ChatModel constructor(
    val from: String = "",
    val to: String = "",
    val message: String = "",
    val time: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.of(ZoneOffset.systemDefault().id)),
    val id: String = UUID.randomUUID().toString(),
    val deliveryState: MessageDeliveryState = MessageDeliveryState.Sent
)

