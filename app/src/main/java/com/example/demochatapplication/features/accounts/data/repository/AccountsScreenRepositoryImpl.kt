@file:OptIn(ExperimentalPagingApi::class, ExperimentalPagingApi::class)

package com.example.demochatapplication.features.accounts.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.core.remote.dto.SearchUserBodyDto
import com.example.demochatapplication.core.remote.util.getResponseBody
import com.example.demochatapplication.features.accounts.data.database.AccountsDatabase
import com.example.demochatapplication.features.accounts.data.database.dao.AccountUserDao
import com.example.demochatapplication.features.accounts.data.database.entities.AccountsUserEntity
import com.example.demochatapplication.features.accounts.data.pagination.AccountsRemoteMediator
import com.example.demochatapplication.features.accounts.domain.model.AccountUserModel
import com.example.demochatapplication.features.accounts.domain.repository.AccountsUserRepository
import com.example.demochatapplication.features.shared.internetconnectivity.NetworkConnectionManager
import com.example.demochatapplication.features.shared.usersettings.UserSettings
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

/**
 * Created by Jatin Vashisht on 19-10-2023.
 */

class AccountsScreenRepositoryImpl @Inject constructor(
    private val accountsDatabase: AccountsDatabase,
    private val accountsUserEntityAndModelMapper: Mapper<AccountsUserEntity, AccountUserModel>,
    private val chatApi: ChatApi,
    private val userSettingsRepository: UserSettingsRepository,
    private val networkConnectionManager: NetworkConnectionManager,
) :
    AccountsUserRepository {

    private var accountUserDao: AccountUserDao = accountsDatabase.accountUserDao

    private var getListFrom = 0

    private lateinit var userSettings: UserSettings

    init {
        CoroutineScope(Dispatchers.IO).launch {
            userSettings = userSettingsRepository.getFirstEntry()
        }
    }

    override suspend fun getAllUsers(shouldLoadFromNetwork: Boolean,): Flow<PagingData<AccountUserModel>> {
        val pageConfig = PagingConfig(
            pageSize = 50,
            prefetchDistance = 20,
        )

        val accountsRemoteMediator = AccountsRemoteMediator(
            networkConnectionManager = networkConnectionManager,
            accountsDatabase = accountsDatabase,
            chatApi = chatApi,
            userSettings = userSettings,
            shouldLoadFromNetwork = shouldLoadFromNetwork
        )

        val accountsUserModelPagingDataFlow = Pager(
            config = pageConfig,
            remoteMediator = accountsRemoteMediator,
        ) {
            accountUserDao.getAllUsers()
        }
            .flow
            .map {accountsUserEntityPagingData->
                accountsUserEntityPagingData.map { accountsUserEntity->
                    accountsUserEntityAndModelMapper.mapAtoB(accountsUserEntity)
                }
            }

        return accountsUserModelPagingDataFlow
    }


    @Throws(Exception::class)
    override suspend fun deleteUser(username: String) {
        try {
            accountUserDao.deleteUsername(username = username)
        } catch (e: Exception) {
            Timber.tag(TAG).d(e.toString())
            throw (e)
        }
    }

    override suspend fun insertUser(accountUserModel: AccountUserModel) {
        try {
            val searchUserEntity = accountsUserEntityAndModelMapper.mapBtoA(accountUserModel)
            accountUserDao.insertUsers(searchUserEntity)
        } catch (e: Exception) {
            Timber.tag(TAG).d(e.toString())
        }
    }


    companion object {
        const val IO_EXCEPTION_MESSAGE = "unable to load data, please try again later"
        const val SOCKET_TIMEOUT_EXCEPTION_MESSAGE = "taking too long to get response"
        const val UNKNOWN_HOST_EXCEPTION_MESSAGE = "unable to reach specified host"
        const val UNKNOWN_EXCEPTION_MESSAGE = "something went wrong, please try again later"
        const val TAG = "searchuserrepositoryimpl"
    }
}