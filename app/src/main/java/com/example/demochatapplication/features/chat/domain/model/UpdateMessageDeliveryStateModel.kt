package com.example.demochatapplication.features.chat.domain.model

import kotlinx.serialization.Serializable


@Serializable()
data class UpdateMessageDeliveryStateModel(
    val from: String,
    val to: String,
    val messageId: String,
    val updatedStatus: String,
)
