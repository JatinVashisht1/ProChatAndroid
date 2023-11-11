package com.example.demochatapplication.features.chat.domain.model

/**
 * Created by Jatin Vashisht on 03-11-2023.
 */
sealed class MessageDeliveryState(val rawString: String) {
    data object Sent: MessageDeliveryState(rawString = "sent")
    data object Received: MessageDeliveryState(rawString = "received")
    data object Read: MessageDeliveryState(rawString = "read")
}
