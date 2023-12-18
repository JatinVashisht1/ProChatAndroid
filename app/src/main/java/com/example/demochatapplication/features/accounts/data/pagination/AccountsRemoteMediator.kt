package com.example.demochatapplication.features.accounts.data.pagination

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.core.remote.dto.Account
import com.example.demochatapplication.core.remote.dto.GetChatAccountsDto
import com.example.demochatapplication.features.accounts.data.database.AccountsDatabase
import com.example.demochatapplication.features.accounts.data.database.entities.AccountsUserEntity
import com.example.demochatapplication.features.searchuseraccounts.data.paging.SearchUserRemoteMediator
import com.example.demochatapplication.features.shared.internetconnectivity.NetworkConnectionManager
import com.example.demochatapplication.features.shared.usersettings.UserSettings
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.net.ssl.SSLException

@OptIn(ExperimentalPagingApi::class)
class AccountsRemoteMediator @Inject constructor(
    private val networkConnectionManager: NetworkConnectionManager,
    private val accountsDatabase: AccountsDatabase,
    private val chatApi: ChatApi,
    private val userSettings: UserSettings,
    private val shouldLoadFromNetwork: Boolean,
): RemoteMediator<Int, AccountsUserEntity>() {
    private val accountsUserDao = accountsDatabase.accountUserDao
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AccountsUserEntity>
    ): MediatorResult {
        try {
            val token = userSettings.token
            val getAccountsResponse = chatApi.getChatAccounts(authorizationHeader = token)

            if (!getAccountsResponse.isSuccessful) {
                throw retrofit2.HttpException(getAccountsResponse)
            }

            val accountsDto = getAccountsResponse.body() ?: GetChatAccountsDto()

            accountsDto.accounts?.let {nonNullAccounts->
                val accountsUserEntityList = accountsDtoToAccountsUserDbEntity(accounts = nonNullAccounts)

                withContext(IO) {
                    accountsDatabase.withTransaction {
                        accountsUserDao.deleteAllUsernames()
                        accountsUserDao.insertUsers(accountsUserEntityList = accountsUserEntityList)
                    }
                }
            }

            return MediatorResult.Success(endOfPaginationReached = true)

        } catch (e: IOException) {
            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: retrofit2.HttpException) {
            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: SSLException) {
            return MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: Exception) {
            Timber.tag(SearchUserRemoteMediator.TAG).d("exception while searching user: $e")
            return MediatorResult.Error(e)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return if (!networkConnectionManager.isInternetAvailable || !shouldLoadFromNetwork) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    private fun accountsDtoToAccountsUserDbEntity (accounts: List<Account>): List<AccountsUserEntity> {
        return accounts.map {
            AccountsUserEntity(username = it.username, primaryId = null)
        }
    }
}