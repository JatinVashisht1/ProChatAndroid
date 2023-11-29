package com.example.demochatapplication.features.searchuseraccounts.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.features.searchuseraccounts.data.database.SearchUserDatabase
import com.example.demochatapplication.features.searchuseraccounts.data.database.entity.SearchUserDatabaseEntity
import com.example.demochatapplication.features.searchuseraccounts.data.paging.SearchUserRemoteMediator
import com.example.demochatapplication.features.searchuseraccounts.domain.model.SearchUserDomainModel
import com.example.demochatapplication.features.searchuseraccounts.domain.repository.SearchUserRepository
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class SearchUserRepoImpl @Inject constructor(
    private val searchUserDatabase: SearchUserDatabase,
    private val userSettingsRepository: UserSettingsRepository,
    private val chatApi: ChatApi,
    private val searchUserDbEntityAndModelMapper: Mapper<SearchUserDatabaseEntity, SearchUserDomainModel>,
): SearchUserRepository {
    private lateinit var username: String
    private lateinit var authorizationHeader: String
    init {
        CoroutineScope(IO).launch {
            val userCredentials = userSettingsRepository.getFirstEntry()
            username = userCredentials.username
            authorizationHeader = userCredentials.token
        }
    }
    override suspend fun searchUser(queryString: String): Flow<PagingData<SearchUserDomainModel>> {
        return withContext(IO) {
            val pagingDataFlow = Pager(
                config = PagingConfig(
                    pageSize = 50,
                    prefetchDistance = 15,
                ),
                remoteMediator = SearchUserRemoteMediator(
                    chatApi = chatApi,
                    searchUserDatabase = searchUserDatabase,
                    token = authorizationHeader,
                    queryString = queryString
                )
            ) {
                searchUserDatabase.searchUserDao.searchAccountByUsername(username = queryString)
            }
                .flow
                .map { pagingData ->
                    pagingData.map { searchUserDatabaseEntity ->
                        searchUserDbEntityAndModelMapper.mapAtoB(searchUserDatabaseEntity)
                    }
                }

            pagingDataFlow
        }
    }
}