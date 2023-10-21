package com.example.demochatapplication.features.accounts.data.repository

import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.Resource
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.core.remote.util.getResponseBody
import com.example.demochatapplication.features.accounts.data.database.SearchUserDatabase
import com.example.demochatapplication.features.accounts.data.database.dao.SearchUserDao
import com.example.demochatapplication.features.accounts.data.database.entities.SearchUserEntity
import com.example.demochatapplication.features.accounts.data.dto.SearchUserBodyDto
import com.example.demochatapplication.features.accounts.domain.model.UserModel
import com.example.demochatapplication.features.accounts.domain.repository.SearchUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

/**
 * Created by Jatin Vashisht on 19-10-2023.
 */
class SearchUserRepositoryImpl @Inject constructor(
    private val searchUserDatabase: SearchUserDatabase,
    private val searchUserEntityAndModelMapper: Mapper<SearchUserEntity, UserModel>,
    private val chatApi: ChatApi
) :
    SearchUserRepository {

    private var searchUserDao: SearchUserDao = searchUserDatabase.searchUserDao

    private var getListFrom = 0

    override suspend fun getAllUsers(): Flow<Resource<List<UserModel>>> = flow {
        emit(Resource.Loading<List<UserModel>>())
        try {
            val searchUserModelList = searchUserDao.getAllUsers().map { searchUserEntity ->
                searchUserEntityAndModelMapper.mapAtoB(searchUserEntity)
            }

            emit(Resource.Success<List<UserModel>>(result = searchUserModelList))
        } catch (unknownException: Exception) {
            Timber.tag(TAG).d(unknownException.toString())
            emit(Resource.Error<List<UserModel>>(UNKNOWN_EXCEPTION_MESSAGE))
        }
    }

    @Throws(Exception::class)
    suspend fun searchUserFromDatabase(username: String): List<UserModel> {
        try {
            val userEntityList = searchUserDao.searchUserFromDatabase(username = username)
            val userModelList = userEntityList.map{ userEntity-> searchUserEntityAndModelMapper.mapAtoB(userEntity) }

            return userModelList

        } catch (e: Exception) {
            Timber.tag(TAG).d(e.toString())
            throw (e)
        }
    }

    @Throws(IOException::class, SocketTimeoutException::class)
    suspend fun searchUserFromServer(username: String): List<UserModel> {
        val searchUserBodyDto = SearchUserBodyDto(username = username)
        val searchUserResponse = chatApi.searchUser(searchUserBodyDto = searchUserBodyDto)
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
                    searchUserEntityAndModelMapper.mapBtoA(it)
                }.toTypedArray()

            searchUserDao.insertUsers(*usersFromServer)
        } catch (e: Exception) {
            Timber.tag(TAG).d(e.toString())
        }

        return searchUserFromDatabase(username = username)
    }


    @Throws(Exception::class)
    override suspend fun deleteUser(username: String) {
        try {
            searchUserDao.deleteUsername(username = username)
        } catch (e: Exception) {
            Timber.tag(TAG).d(e.toString())
            throw (e)
        }
    }

    override suspend fun insertUser(userModel: UserModel) {
        try {
            val searchUserEntity = searchUserEntityAndModelMapper.mapBtoA(userModel)
            searchUserDao.insertUsers(searchUserEntity)
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