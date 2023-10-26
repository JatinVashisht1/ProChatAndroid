package com.example.demochatapplication.core.remote

import com.example.demochatapplication.core.Constants
import com.example.demochatapplication.core.remote.dto.GetChatAccountsDto
import com.example.demochatapplication.core.remote.dto.SearchUserBodyDto
import com.example.demochatapplication.core.remote.dto.SearchUserResponseDto
import com.example.demochatapplication.features.login.data.dto.SignInBodyDto
import com.example.demochatapplication.features.login.data.dto.SignInResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ChatApi {
    @POST("/user/signin")
    suspend fun signInUser(@Body signInBodyDto: SignInBodyDto): Response<SignInResponseDto>

    @GET("/user/searchUser")
    suspend fun searchUser(
        @Body searchUserBodyDto: SearchUserBodyDto,
        @Header(Constants.AUTHORIZATION_HEADER) authorizationHeader: String
    ): Response<SearchUserResponseDto>

    @GET("/chat/getuseraccounts")
    suspend fun getChatAccounts(
        @Header(Constants.AUTHORIZATION_HEADER) authorizationHeader: String,
    ): Response<GetChatAccountsDto>
}