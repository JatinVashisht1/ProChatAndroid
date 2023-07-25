package com.example.demochatapplication.features.login.data.remote

import com.example.demochatapplication.features.login.data.dto.SignInBodyDto
import com.example.demochatapplication.features.login.data.dto.SignInResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatApi {
    @POST("/user/signin")
    suspend fun signInUser(@Body signInBodyDto: SignInBodyDto): Response<SignInResponseDto>
}