package com.example.demochatapplication.features.accounts.domain.pagination

import com.example.demochatapplication.core.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Created by Jatin Vashisht on 20-10-2023.
 */
interface Paginator<T> {
    val result: MutableStateFlow<T>
    suspend fun getPaginatedData(username: String)

    suspend fun initNewSearch(username: String)
}