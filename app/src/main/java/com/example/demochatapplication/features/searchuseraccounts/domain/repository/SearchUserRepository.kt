package com.example.demochatapplication.features.searchuseraccounts.domain.repository

import androidx.paging.PagingData
import com.example.demochatapplication.features.searchuseraccounts.domain.model.SearchUserDomainModel
import kotlinx.coroutines.flow.Flow

interface SearchUserRepository {
    suspend fun searchUser(username: String): Flow<PagingData<SearchUserDomainModel>>
}