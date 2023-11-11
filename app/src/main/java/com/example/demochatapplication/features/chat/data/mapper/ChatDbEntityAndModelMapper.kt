package com.example.demochatapplication.features.chat.data.mapper

import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.features.chat.data.database.entity.ChatDbEntity
import com.example.demochatapplication.features.chat.domain.model.ChatModel
import com.example.demochatapplication.features.chat.domain.model.MessageDeliveryState
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.UUID
import javax.inject.Inject

/**
 * Created by Jatin Vashisht on 28-10-2023.
 */
class ChatDbEntityAndModelMapper : Mapper<ChatDbEntity, ChatModel> {
    override fun mapAtoB(objectTypeA: ChatDbEntity): ChatModel {
//        val instance = Instant.ofEpochSecond(objectTypeA.timeStamp)
//        val localDateTime = instance.atZone(ZoneId.systemDefault()).toLocalDateTime()
        return ChatModel(
            from = objectTypeA.from,
            to = objectTypeA.to,
            message = objectTypeA.message,
            time = objectTypeA.timeStamp,
            id = objectTypeA.primaryKey,
            deliveryState = objectTypeA.deliveryStatus
        )
    }

    override fun mapBtoA(objectTypeB: ChatModel): ChatDbEntity {
//        val time = objectTypeB.time.toEpochSecond(ZoneOffset.UTC)

        return ChatDbEntity(
            from = objectTypeB.from,
            to = objectTypeB.to,
            message = objectTypeB.message,
            timeStamp = objectTypeB.time,
            primaryKey = objectTypeB.id,
            deliveryStatus = objectTypeB.deliveryState
        )
    }
}