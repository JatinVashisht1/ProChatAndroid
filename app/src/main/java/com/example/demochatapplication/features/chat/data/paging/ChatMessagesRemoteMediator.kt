package com.example.demochatapplication.features.chat.data.paging

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
import timber.log.Timber
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
        /*
        val loadKey = when(loadType) {
            LoadType.REFRESH -> {
                null
            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()

                lastItem?.timeStamp ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }
        */

        val chatMessagesBetween2UsersResponse = chatApi.getChatMessagesBetween2Users(authorizationHeader = authorizationHeader, anotherUsername = anotherUser)

        if (!chatMessagesBetween2UsersResponse.isSuccessful) {
            Timber.tag(TAG).d("request did not retured a successful response: ${chatMessagesBetween2UsersResponse.errorBody()}")
            return MediatorResult.Error(Exception("Unable to load chat messages. Please try again later"))
        }

        val chatMessagesDto = chatMessagesBetween2UsersResponse.body()?: GetChatMessagesBetween2UsersDto(username1 = currentUser, username2 = anotherUser)
        val chatDbEntityList = chatMessageDtoAndDbEntityMapper.mapAtoB(chatMessagesDto)

        chatMessageDao.deleteAndInsertChats(username1 = currentUser, username2 = anotherUser, chatDbEntityList)

        return MediatorResult.Success(endOfPaginationReached = true)
    }

    companion object {
        const val TAG = "chatmessagesremotemediator"
    }
}