package com.example.demochatapplication.core.remote

import com.example.demochatapplication.core.Constants
import com.example.demochatapplication.core.remote.dto.GetChatAccountsDto
import com.example.demochatapplication.core.remote.dto.GetChatMessagesBetween2UsersBodyDto
import com.example.demochatapplication.core.remote.dto.GetChatMessagesBetween2UsersDto
import com.example.demochatapplication.core.remote.dto.SearchUserBodyDto
import com.example.demochatapplication.core.remote.dto.SearchUserResponseDto
import com.example.demochatapplication.features.login.data.dto.SignInBodyDto
import com.example.demochatapplication.features.login.data.dto.SignInResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("/chat/getuserschat/{anotherUsername}")
    @Headers("Content-Type: application/json")
    suspend fun getChatMessagesBetween2Users(
        @Header(Constants.AUTHORIZATION_HEADER) authorizationHeader: String,
        @Path("anotherUsername") anotherUsername: String,
    ): Response<GetChatMessagesBetween2UsersDto>
}