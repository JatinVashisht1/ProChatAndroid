package com.example.demochatapplication.features.chat.data.paging

import android.net.http.HttpException
import android.net.http.NetworkException
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.core.remote.dto.GetChatMessagesBetween2UsersDto
import com.example.demochatapplication.features.chat.data.database.ChatDatabase
import com.example.demochatapplication.features.chat.data.database.entity.ChatDbEntity
import com.example.demochatapplication.features.chat.data.mapper.ChatMessageDtoAndDbEntityMapper
import com.example.demochatapplication.features.chat.domain.exceptions.DatabaseOperationException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@ExperimentalPagingApi
class ChatMessagesRemoteMediator @Inject constructor (
    private val chatMessageDatabase: ChatDatabase,
    private val chatApi: ChatApi,
    private val chatMessageDtoAndDbEntityMapper: Mapper<GetChatMessagesBetween2UsersDto, List<ChatDbEntity>>,
    private val currentUser: String,
    private val anotherUser: String,
    private val authorizationHeader: String,
): RemoteMediator<Int, ChatDbEntity>() {
    private val chatMessageDao = chatMessageDatabase.chatDao
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ChatDbEntity>
    ): MediatorResult {
        try {
            Timber.tag(TAG).d("load type is ${loadType.name}")

            val chatMessagesBetween2UsersResponse = chatApi.getChatMessagesBetween2Users(authorizationHeader = authorizationHeader, anotherUsername = anotherUser)

            if (!chatMessagesBetween2UsersResponse.isSuccessful) {
                Timber.tag(TAG).d("request did not returned a successful response: ${chatMessagesBetween2UsersResponse.errorBody()}")
//                return MediatorResult.Error(Exception("Unable to load chat messages. Please try again later"))
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            val chatMessagesDto = chatMessagesBetween2UsersResponse.body()?: GetChatMessagesBetween2UsersDto(username1 = currentUser, username2 = anotherUser)
            val chatDbEntityList = chatMessageDtoAndDbEntityMapper.mapAtoB(chatMessagesDto)

            try {
                chatMessageDao.deleteAndInsertChats(username1 = currentUser, username2 = anotherUser, chatDbEntityList)
            } catch (e: Exception) {
                throw DatabaseOperationException
            }

            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: IOException) {
//            return MediatorResult.Error(e)
            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: retrofit2.HttpException) {
//            return MediatorResult.Error(e)
            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    companion object {
        const val TAG = "chatmessagesremotemediator"
    }
}