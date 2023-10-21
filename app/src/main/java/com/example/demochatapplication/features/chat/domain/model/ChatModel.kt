package com.example.demochatapplication.features.chat.domain.model

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable()
data class ChatModel constructor(
    val from: String = "",
    val to: String = "",
    val message: String = "",
    val time: String = "",
    val id: String = "",
)
