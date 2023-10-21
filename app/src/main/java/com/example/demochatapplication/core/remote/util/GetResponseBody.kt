package com.example.demochatapplication.core.remote.util

import com.example.demochatapplication.features.accounts.data.dto.SearchUserBodyDto
import com.example.demochatapplication.features.accounts.data.dto.SearchUserResponseDto
import retrofit2.Response

/**
 * Created by Jatin Vashisht on 19-10-2023.
 */

fun Response<SearchUserResponseDto>.getResponseBody(): SearchUserResponseDto =
    body() ?: SearchUserResponseDto(searchString = "", users = emptyList())
