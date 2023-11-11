package com.example.demochatapplication.features.chat.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.demochatapplication.features.chat.data.database.entity.ChatDbEntity
import com.example.demochatapplication.features.chat.data.mapper.MessageDeliveryStateAndStringMapper

/**
 * Created by Jatin Vashisht on 27-10-2023.
 */

@Database(entities = [ChatDbEntity::class], version = 1,)
@TypeConverters(MessageDeliveryStateAndStringMapper::class)
abstract class ChatDatabase: RoomDatabase() {
    abstract val chatDao: ChatDao

    companion object {
        const val CHAT_DB_NAME = "chatdatabase"
    }
}