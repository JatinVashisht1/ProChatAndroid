package com.example.demochatapplication.features.chat.data.database.entity

import com.example.demochatapplication.features.chat.domain.model.MessageDeliveryState

data class UpdateAllMessageDeliveryStatusBetween2UsersEntity(
    val from: String,
    val to: String,
    val messageDeliveryState: MessageDeliveryState,
)
