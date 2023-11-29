package com.example.demochatapplication.features.accounts.domain.repository

import com.example.demochatapplication.core.Resource
import com.example.demochatapplication.features.accounts.domain.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Created by Jatin Vashisht on 19-10-2023.
 */
interface AccountsUserRepository {
    suspend fun getAllUsers(shouldLoadFromNetwork: Boolean): Flow<List<UserModel>>

//    suspend fun searchUser(username: String,): List<UserModel>

    suspend fun deleteUser(username: String)

    suspend fun insertUser(userModel: UserModel)
}