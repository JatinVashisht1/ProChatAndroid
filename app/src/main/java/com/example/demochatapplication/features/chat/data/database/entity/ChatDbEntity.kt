package com.example.demochatapplication.features.chat.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.demochatapplication.features.chat.domain.model.MessageDeliveryState

/**
 * Created by Jatin Vashisht on 27-10-2023.
 */

@Entity("chatdbentity",)
data class ChatDbEntity(
    val from: String,
    val to: String,
    val message: String,
    val timeStamp: Long,
    val deliveryStatus: MessageDeliveryState,
    val deletedByReceiver: Boolean,
    @PrimaryKey
    val primaryKey: String,
)
