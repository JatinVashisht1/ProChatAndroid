package com.example.demochatapplication.features.searchuseraccounts.data.paging

import android.net.http.HttpException
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.core.remote.dto.SearchUserBodyDto
import com.example.demochatapplication.features.searchuseraccounts.data.database.SearchUserDatabase
import com.example.demochatapplication.features.searchuseraccounts.data.database.entity.SearchUserDatabaseEntity
import java.io.IOException
import javax.inject.Inject
import javax.net.ssl.SSLException

@OptIn(ExperimentalPagingApi::class)
class SearchUserRemoteMediator @Inject constructor(
    private val chatApi: ChatApi,
    private val searchUserDatabase: SearchUserDatabase,
    private val username: String,
    private val token: String,
) : RemoteMediator<Int, SearchUserDatabaseEntity>() {
    private val searchUserDao = searchUserDatabase.searchUserDao
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SearchUserDatabaseEntity>
    ): MediatorResult {
        try {
            val searchUserResponse = chatApi.searchUser(searchUserBodyDto = SearchUserBodyDto(username = username), authorizationHeader = token)
            if (!searchUserResponse.isSuccessful) {
                throw retrofit2.HttpException(searchUserResponse)
            }

            val searchUserBody = searchUserResponse.body()

            searchUserBody?.let {
                val usernameList = it.users
                val searchUsernameDbEntityList = usernameList.map {username ->
                    SearchUserDatabaseEntity(username = username)
                }

                searchUserDao.insertSearchUserEntity(searchUsernameDbEntityList)
            }

            return MediatorResult.Success(endOfPaginationReached = true)

        } catch (e: IOException) {
            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: retrofit2.HttpException) {
            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: SSLException) {
            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }
}