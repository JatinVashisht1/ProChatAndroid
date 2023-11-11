package com.example.demochatapplication.core.remote.util

import com.example.demochatapplication.core.remote.dto.GetChatAccountsDto
import com.example.demochatapplication.core.remote.dto.SearchUserResponseDto
import retrofit2.Response

/**
 * Created by Jatin Vashisht on 19-10-2023.
 */

fun Response<SearchUserResponseDto>.getResponseBody(): SearchUserResponseDto =
    body() ?: SearchUserResponseDto(searchString = "", users = emptyList())


fun Response<GetChatAccountsDto>.getResponseBody(): GetChatAccountsDto = body()?: GetChatAccountsDto(accounts = emptyList(), user = "")