package com.example.demochatapplication.features.chat.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.demochatapplication.features.chat.data.database.entity.ChatDbEntity

/**
 * Created by Jatin Vashisht on 27-10-2023.
 */

@Dao
interface ChatDao {

    @Query("SELECT * FROM chatdbentity WHERE (`from` = :username1 AND `to` = :username2) OR (`from` = :username2 AND `to` = :username1) ORDER BY `timeStamp` ASC")
    suspend fun getChatBetween2Users(username1: String, username2: String): List<ChatDbEntity>

    @Query("SELECT COUNT(*) FROM chatdbentity WHERE (`from` = :username1 AND `to` = :username2) OR (`from` = :username2 AND `to` = :username1)")
    suspend fun getChatMessagesCount(username1: String, username2: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChatMessage(message: ChatDbEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChatMessage(message: List<ChatDbEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertChatMessage(vararg message: ChatDbEntity)

    @Delete()
    suspend fun deleteChatMessage(message: ChatDbEntity)

    @Query("DELETE FROM chatdbentity where (`from` = :username1 AND `to` = :username2) OR (`from` = :username2 AND `to` = :username1)")
    suspend fun clearChatMessagesBetween2Users(username1: String, username2: String)

    @Query("SELECT COUNT(*) FROM chatdbentity WHERE primaryKey = :messageId")
    suspend fun doesMessageExist(messageId: String): Int

    @Transaction()
    suspend fun deleteAndInsertChats(
        username1: String,
        username2: String,
        chatDbEntity: List<ChatDbEntity>
    ) {
        clearChatMessagesBetween2Users(username1 = username1, username2 = username2)
        insertChatMessage(chatDbEntity)
    }

    @Transaction()
    suspend fun deleteAndInsertChats(
        username1: String,
        username2: String,
        chatDbEntity: ChatDbEntity
    ) {
        clearChatMessagesBetween2Users(username1 = username1, username2 = username2)
        insertChatMessage(chatDbEntity)
    }
}