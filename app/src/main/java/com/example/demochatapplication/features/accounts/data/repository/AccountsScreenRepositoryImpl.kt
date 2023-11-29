package com.example.demochatapplication.features.accounts.data.repository

import android.accounts.NetworkErrorException
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.Resource
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.core.remote.dto.SearchUserBodyDto
import com.example.demochatapplication.core.remote.util.getResponseBody
import com.example.demochatapplication.features.accounts.data.database.AccountsDatabase
import com.example.demochatapplication.features.accounts.data.database.dao.AccountUserDao
import com.example.demochatapplication.features.accounts.data.database.entities.AccountsUserEntity
import com.example.demochatapplication.features.accounts.domain.model.UserModel
import com.example.demochatapplication.features.accounts.domain.repository.AccountsUserRepository
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val accountsUserEntityAndModelMapper: Mapper<AccountsUserEntity, UserModel>,
    private val chatApi: ChatApi,
    private val userSettingsRepository: UserSettingsRepository,
) :
    AccountsUserRepository {

    private var accountUserDao: AccountUserDao = accountsDatabase.accountUserDao

    private var getListFrom = 0

    private lateinit var authorizationHeader: String

    val searchUserEntityList = accountUserDao.getAllUsers()

    private val users =
        MutableStateFlow<Resource<List<UserModel>>>(Resource.Loading<List<UserModel>>())

    init {
        CoroutineScope(Dispatchers.IO).launch {
            authorizationHeader = userSettingsRepository.getFirstEntry().token
        }
    }

    override suspend fun getAllUsers(shouldLoadFromNetwork: Boolean): Flow<List<UserModel>> {
        val accountsCount = accountUserDao.getUserAccountsCount()

        if (shouldLoadFromNetwork || accountsCount == 0) {
            Timber.tag(TAG).d("authorizationheader: $authorizationHeader")
            val chatAccountsResponse =
                chatApi.getChatAccounts(authorizationHeader = authorizationHeader)

            val isSuccessful = chatAccountsResponse.isSuccessful

            if (!isSuccessful) {
                throw NetworkErrorException("Unable to fetch data from server. Please try again later")
            }

            val chatAccountsDto = chatAccountsResponse.body()

            Timber.tag(TAG)
                .d("chataccountsdto: $chatAccountsDto, isSuccessful: $isSuccessful, raw: ${chatAccountsResponse.raw()}")

            val accountsUserEntity = chatAccountsDto?.accounts?.map { account ->
                AccountsUserEntity(username = account.username ?: "", primaryId = null)
            }
                ?.toTypedArray()



            accountsUserEntity?.let {
                accountUserDao.insertUsers(*it)
            }
        }

        val userModelFlow = accountUserDao.getAllUsers()
            .map { accountUserEntityList ->
                val accountsModelList = accountUserEntityList.map {
                    accountsUserEntityAndModelMapper.mapAtoB(it)
                }

                return@map accountsModelList
            }

        return userModelFlow
    }

    @Throws(Exception::class)
    suspend fun searchUserFromDatabase(username: String): List<UserModel> {
        try {
            val userEntityList = accountUserDao.searchUserFromDatabase(username = username)
            val userModelList = userEntityList.map { userEntity ->
                accountsUserEntityAndModelMapper.mapAtoB(userEntity)
            }

            return userModelList

        } catch (e: Exception) {
            Timber.tag(TAG).d(e.toString())
            throw (e)
        }
    }

    @Throws(IOException::class, SocketTimeoutException::class)
    suspend fun searchUserFromServer(username: String): List<UserModel> {
        val searchUserBodyDto = SearchUserBodyDto(username = username)
        val searchUserResponse = chatApi.searchUser(
            searchUserBodyDto = searchUserBodyDto,
            authorizationHeader = authorizationHeader
        )
        val isRequestSuccessful = searchUserResponse.isSuccessful

        if (isRequestSuccessful) {
            val searchUserResponseBody = searchUserResponse.getResponseBody()
            val userModelList = searchUserResponseBody.users.map { searchResponseUsername ->
                UserModel(username = searchResponseUsername)
            }

            return userModelList
        }

        val responseHttpCode = searchUserResponse.code()

        if (responseHttpCode in 400..499) {
            throw IOException(IO_EXCEPTION_MESSAGE)
        } else {
            throw SocketTimeoutException(SOCKET_TIMEOUT_EXCEPTION_MESSAGE)
        }
    }

    override suspend fun searchUser(username: String): List<UserModel> {
        try {
            val usersFromServer = searchUserFromServer(username = username)
                .map {
                    accountsUserEntityAndModelMapper.mapBtoA(it)
                }.toTypedArray()

            accountUserDao.insertUsers(*usersFromServer)
        } catch (e: Exception) {
            Timber.tag(TAG).d(e.toString())
        }

        return searchUserFromDatabase(username = username)
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

    override suspend fun insertUser(userModel: UserModel) {
        try {
            val searchUserEntity = accountsUserEntityAndModelMapper.mapBtoA(userModel)
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