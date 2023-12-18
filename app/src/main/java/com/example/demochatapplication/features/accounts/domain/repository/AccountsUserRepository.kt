package com.example.demochatapplication.features.accounts.domain.repository

import androidx.paging.PagingData
import com.example.demochatapplication.features.accounts.domain.model.AccountUserModel
import kotlinx.coroutines.flow.Flow

/**
 * Created by Jatin Vashisht on 19-10-2023.
 */
interface AccountsUserRepository {
    suspend fun getAllUsers(shouldLoadFromNetwork: Boolean): Flow<PagingData<AccountUserModel>>

//    suspend fun searchUser(username: String,): List<UserModel>

    suspend fun deleteUser(username: String)

    suspend fun insertUser(accountUserModel: AccountUserModel)
}