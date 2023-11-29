package com.example.demochatapplication.features.chat.data.mapper

import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.features.chat.data.database.entity.ChatDbEntity
import com.example.demochatapplication.features.chat.domain.model.ChatScreenUiModel

/**
 * Created by Jatin Vashisht on 28-10-2023.
 */
class ChatDbEntityAndModelMapper : Mapper<ChatDbEntity, ChatScreenUiModel.ChatModel> {
    override fun mapAtoB(objectTypeA: ChatDbEntity): ChatScreenUiModel.ChatModel {
//        val instance = Instant.ofEpochSecond(objectTypeA.timeStamp)
//        val localDateTime = instance.atZone(ZoneId.systemDefault()).toLocalDateTime()
        return ChatScreenUiModel.ChatModel(
            from = objectTypeA.from,
            to = objectTypeA.to,
            message = objectTypeA.message,
            timeInMillis = objectTypeA.timeStamp,
            id = objectTypeA.primaryKey,
            deliveryState = objectTypeA.deliveryStatus
        )
    }

    override fun mapBtoA(objectTypeB: ChatScreenUiModel.ChatModel): ChatDbEntity {
//        val time = objectTypeB.time.toEpochSecond(ZoneOffset.UTC)

        return ChatDbEntity(
            from = objectTypeB.from,
            to = objectTypeB.to,
            message = objectTypeB.message,
            timeStamp = objectTypeB.timeInMillis,
            primaryKey = objectTypeB.id,
            deliveryStatus = objectTypeB.deliveryState
        )
    }
}