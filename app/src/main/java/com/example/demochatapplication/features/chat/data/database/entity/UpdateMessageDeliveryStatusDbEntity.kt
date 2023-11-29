package com.example.demochatapplication.features.chat.data.database.entity

import com.example.demochatapplication.features.chat.domain.model.MessageDeliveryState

data class UpdateMessageDeliveryStatusDbEntity(
    val primaryKey: String,
    val deliveryStatus: MessageDeliveryState
)
