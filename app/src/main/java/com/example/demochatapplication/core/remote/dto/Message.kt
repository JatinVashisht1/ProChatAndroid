package com.example.demochatapplication.core.remote.dto

data class Message(
    val createdDate: String,
    val createdDay: String,
    val createdMonth: String,
    val createdTime: String,
    val createdYear: String,
    val deliveryStatus: String,
    val message: String,
    val messageId: String,
    val receiverUsername: String,
    val senderUsername: String,
    val deletedByReceiver: Boolean,
)