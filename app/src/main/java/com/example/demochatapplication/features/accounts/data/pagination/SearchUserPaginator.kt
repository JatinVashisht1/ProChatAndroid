package com.example.demochatapplication.features.accounts.data.pagination

import com.example.demochatapplication.features.accounts.domain.model.UserModel
import com.example.demochatapplication.features.accounts.domain.pagination.Paginator
import com.example.demochatapplication.features.accounts.domain.repository.AccountsUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Jatin Vashisht on 20-10-2023.
 */
class SearchUserPaginator @Inject() constructor(
    private val accountsUserRepository: AccountsUserRepository
) : Paginator<List<UserModel>> {
    override val result: MutableStateFlow<List<UserModel>> = MutableStateFlow(emptyList())
    var currentIndex = 0
    override suspend fun getPaginatedData(
        username: String,
    ) {
        try {
            val userModelList = accountsUserRepository.searchUser(username = username,)
            val userModelListSize = userModelList.size
            val oldList = result.value

            if (currentIndex + 10 < userModelListSize) {
                val newList = oldList + userModelList.subList(currentIndex, currentIndex + 10)
                result.value = newList
                currentIndex += 10
            } else if (currentIndex < userModelListSize) {
                result.value = oldList + userModelList.subList(currentIndex, userModelListSize)

                currentIndex += 10
            }
        } catch (e: Exception) {
            Timber.tag(TAG).d(e.toString())
            throw(e)
        }
    }

    override suspend fun initNewSearch(username: String) {
            try {
               result.value = emptyList()
                currentIndex = 0

                getPaginatedData(username = username)
            } catch (e: Exception) {
                Timber.tag(TAG).d(e.toString())
                throw(e)
            }
        }

    companion object {
        const val TAG = "paginatorimplementation"
    }
}