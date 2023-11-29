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
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.net.ssl.SSLException

@OptIn(ExperimentalPagingApi::class)
class SearchUserRemoteMediator @Inject constructor(
    private val chatApi: ChatApi,
    private val searchUserDatabase: SearchUserDatabase,
    private val token: String,
    private val queryString: String,
) : RemoteMediator<Int, SearchUserDatabaseEntity>() {
    private val searchUserDao = searchUserDatabase.searchUserDao
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SearchUserDatabaseEntity>
    ): MediatorResult {
        try {
            val searchUserResponse = chatApi.searchUser(authorizationHeader = token, searchUser = queryString,)
            if (!searchUserResponse.isSuccessful) {
                throw retrofit2.HttpException(searchUserResponse)
            }

            val searchUserBody = searchUserResponse.body()

            searchUserBody?.let {
                val usernameList = it.users
                val searchUsernameDbEntityList = usernameList.map {username ->
                    SearchUserDatabaseEntity(username = username)
                }

                Timber.tag(TAG).d("searchuserbody: ${it.users}")


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
            Timber.tag(TAG).d("exception while searching user: $e")
            return MediatorResult.Error(e)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return if (queryString.isBlank()) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    companion object {
        const val TAG = "searchuserremotemediator"
    }
}