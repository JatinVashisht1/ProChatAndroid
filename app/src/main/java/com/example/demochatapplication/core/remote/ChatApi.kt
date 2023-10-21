package com.example.demochatapplication.core.remote

import com.example.demochatapplication.features.accounts.data.dto.SearchUserBodyDto
import com.example.demochatapplication.features.accounts.data.dto.SearchUserResponseDto
import com.example.demochatapplication.features.login.data.dto.SignInBodyDto
import com.example.demochatapplication.features.login.data.dto.SignInResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApi {
    @POST("/user/signin")
    suspend fun signInUser(@Body signInBodyDto: SignInBodyDto): Response<SignInResponseDto>

    @POST("/user/searchUser")
    suspend fun searchUser(@Body searchUserBodyDto: SearchUserBodyDto): Response<SearchUserResponseDto>
}