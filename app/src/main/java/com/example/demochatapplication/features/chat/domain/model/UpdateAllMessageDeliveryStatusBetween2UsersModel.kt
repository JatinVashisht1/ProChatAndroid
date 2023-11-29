package com.example.demochatapplication.features.chat.domain.model

import kotlinx.serialization.Serializable

@Serializable()
data class UpdateAllMessageDeliveryStatusBetween2UsersModel(
    val from: String,
    val to: String,
    val deliveryStatus: String,
)
