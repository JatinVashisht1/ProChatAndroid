package com.example.demochatapplication.features.chat.data.mapper

import androidx.room.TypeConverter
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.features.chat.domain.exceptions.InvalidMessageDeliveryStateStringException
import com.example.demochatapplication.features.chat.domain.model.MessageDeliveryState

/**
 * Created by Jatin Vashisht on 03-11-2023.
 */
class MessageDeliveryStateAndStringMapper : Mapper<MessageDeliveryState, String> {

    @TypeConverter
    override fun mapAtoB(objectTypeA: MessageDeliveryState): String {
        return objectTypeA.rawString
    }

    @TypeConverter
    override fun mapBtoA(objectTypeB: String): MessageDeliveryState {
        return when(objectTypeB) {
            "read" -> {
                MessageDeliveryState.Read
            }

            "sent" -> {
                MessageDeliveryState.Sent
            }

            "received" -> {
                MessageDeliveryState.Received
            }

            else -> {
                throw InvalidMessageDeliveryStateStringException
            }
        }
    }
}