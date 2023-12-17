package com.example.demochatapplication.core.remote

import com.example.demochatapplication.core.remote.dto.GetChatAccountsDto
import com.example.demochatapplication.core.remote.dto.GetChatMessagesBetween2UsersDto
import com.example.demochatapplication.core.remote.dto.SearchUserResponseDto
import com.example.demochatapplication.core.remote.dto.SignUpUserBodyDto
import com.example.demochatapplication.core.remote.dto.SignUpUserResponseDto
import com.example.demochatapplication.core.remote.dto.UpdateFirebaseRegistrationTokenBody
import com.example.demochatapplication.core.remote.dto.UpdateFirebaseTokenResponseDto
import com.example.demochatapplication.core.remote.dto.SignInBodyDto
import com.example.demochatapplication.core.remote.dto.SignInResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatApi {
    @POST("/user/signin")
    suspend fun signInUser(@Body signInBodyDto: SignInBodyDto): Response<SignInResponseDto>

    @POST("/user/signup")
    suspend fun signUpUser(@Body signUpUserBodyDto: SignUpUserBodyDto): Response<SignUpUserResponseDto>

    @GET("/user/searchuser/{$SEARCH_USERNAME}")
    suspend fun searchUser(
        @Path(SEARCH_USERNAME) searchUser: String,
        @Header(AUTHORIZATION_HEADER) authorizationHeader: String
    ): Response<SearchUserResponseDto>

    @GET("/chat/getuseraccounts")
    suspend fun getChatAccounts(
        @Header(AUTHORIZATION_HEADER) authorizationHeader: String,
    ): Response<GetChatAccountsDto>

    @GET("/chat/getuserschat/{$ANOTHER_USERNAME_URL_PARAM}")
    @Headers("Content-Type: application/json")
    suspend fun getChatMessagesBetween2Users(
        @Header(AUTHORIZATION_HEADER) authorizationHeader: String,
        @Path(ANOTHER_USERNAME_URL_PARAM) anotherUsername: String,
    ): Response<GetChatMessagesBetween2UsersDto>

    @POST("/user/updatefirebasetoken")
    suspend fun updateUserFirebaseRegistrationToken(
        @Header(AUTHORIZATION_HEADER) authorizationHeader: String,
        @Body updateFirebaseRegistrationTokenBody: UpdateFirebaseRegistrationTokenBody
    ): Response<UpdateFirebaseTokenResponseDto>

    companion object {
        const val ANOTHER_USERNAME_URL_PARAM = "anotherUsername"
        const val SEARCH_USERNAME = "queryUser"
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}