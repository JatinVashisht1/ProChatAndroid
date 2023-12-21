package com.example.demochatapplication.features.chat.data.mapper

import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.remote.dto.GetChatMessagesBetween2UsersDto
import com.example.demochatapplication.core.remote.dto.Message
import com.example.demochatapplication.features.chat.data.database.entity.ChatDbEntity
import com.example.demochatapplication.features.chat.domain.model.MessageDeliveryState
import javax.inject.Inject

class ChatMessageDtoAndDbEntityMapper @Inject constructor(
    private val messageDeliveryStateAndStringMapper: Mapper<MessageDeliveryState, String>
) :
    Mapper<GetChatMessagesBetween2UsersDto, List<ChatDbEntity>> {
    override fun mapAtoB(objectTypeA: GetChatMessagesBetween2UsersDto): List<ChatDbEntity> {
        val chatDbEntityList = objectTypeA.messages.map { message ->
            val deliveryState = messageDeliveryStateAndStringMapper.mapBtoA(message.deliveryStatus)
            ChatDbEntity(
                from = message.senderUsername,
                to = message.receiverUsername,
                message = message.message,
                timeStamp = (message.createdTime.toLong() / 1000),
                primaryKey = message.messageId,
                deliveryStatus = deliveryState,
                deletedByReceiver = message.deletedByReceiver,
            )
        }

        return chatDbEntityList
    }

    override fun mapBtoA(objectTypeB: List<ChatDbEntity>): GetChatMessagesBetween2UsersDto {
        // will never require
        return GetChatMessagesBetween2UsersDto()
    }
}