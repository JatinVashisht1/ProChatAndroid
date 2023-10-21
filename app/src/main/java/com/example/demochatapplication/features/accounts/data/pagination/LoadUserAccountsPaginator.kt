package com.example.demochatapplication.features.accounts.data.pagination

import com.example.demochatapplication.features.accounts.domain.model.UserModel
import com.example.demochatapplication.features.accounts.domain.pagination.Paginator
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

/**
 * Created by Jatin Vashisht on 20-10-2023.
 */
class LoadUserAccountsPaginator @Inject() constructor(): Paginator<List<UserModel>> {
    override val result: MutableStateFlow<List<UserModel>> = MutableStateFlow(emptyList())

    override suspend fun getPaginatedData(username: String) {

    }

    override suspend fun initNewSearch(username: String) {
        TODO("Not yet implemented")
    }
}