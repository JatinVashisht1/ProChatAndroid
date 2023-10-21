package com.example.demochatapplication.features.accounts.domain.repository

import com.example.demochatapplication.core.Resource
import com.example.demochatapplication.features.accounts.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

/**
 * Created by Jatin Vashisht on 19-10-2023.
 */
interface SearchUserRepository {
    suspend fun getAllUsers(): Flow<Resource<List<UserModel>>>

    suspend fun searchUser(username: String,): List<UserModel>

    suspend fun deleteUser(username: String)

    suspend fun insertUser(userModel: UserModel)

//    suspend fun searchUser(username: String):
}