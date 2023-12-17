package com.example.demochatapplication.features.authentication.data.repository

import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.features.authentication.core.UnsuccessfulLoginException
import com.example.demochatapplication.core.remote.dto.SignInBodyDto
import com.example.demochatapplication.core.remote.dto.SignInResponseDto
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.core.remote.dto.SignUpUserBodyDto
import com.example.demochatapplication.core.remote.dto.SignUpUserResponseDto
import com.example.demochatapplication.features.authentication.core.UnSuccessfulSignUpException
import com.example.demochatapplication.features.authentication.data.mapper.SignUpBodyDtoAndModelMapper
import com.example.demochatapplication.features.authentication.data.mapper.SignUpUserResponseDtoAndModelMapper
import com.example.demochatapplication.features.authentication.domain.model.SignInBodyEntity
import com.example.demochatapplication.features.authentication.domain.model.SignInResponseEntity
import com.example.demochatapplication.features.authentication.domain.model.SignUpBodyModel
import com.example.demochatapplication.features.authentication.domain.model.SignUpResponseModel
import com.example.demochatapplication.features.authentication.domain.repository.IAuthenticationRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val signInDtoAndEntityMapper: Mapper<SignInBodyDto, SignInBodyEntity>,
    private val signInResponseDtoAndEntityMapper: Mapper<SignInResponseDto, SignInResponseEntity>,
    private val signUpBodyDtoAndModelMapper: Mapper<SignUpUserBodyDto, SignUpBodyModel>,
    private val signUpUserResponseDtoAndModelMapper: Mapper<SignUpUserResponseDto, SignUpResponseModel>
) : IAuthenticationRepository {
    companion object {
        const val TAG = "authenticationrepository"
    }

    @kotlin.jvm.Throws(UnsuccessfulLoginException::class)
    override suspend fun signInUser(signInBodyEntity: SignInBodyEntity): SignInResponseEntity {
        return withContext(IO) {
            val signInBodyDto = signInDtoAndEntityMapper.mapBtoA(signInBodyEntity)
            Timber.tag(TAG).d("sigin in body is $signInBodyDto")
            val loginUserResponse = chatApi.signInUser(signInBodyDto)

            if (!loginUserResponse.isSuccessful) {
                throw UnsuccessfulLoginException(loginUserResponse.message())
            }

            val signInResponseDto = loginUserResponse.body()
                ?: throw UnsuccessfulLoginException("Empty response body.")

            val signInResponseEntity = signInResponseDtoAndEntityMapper.mapAtoB(signInResponseDto)

            signInResponseEntity
        }
    }

    @kotlin.jvm.Throws(UnSuccessfulSignUpException::class)
    override suspend fun signUpUser(signUpBodyModel: SignUpBodyModel): SignUpResponseModel {
        return withContext(IO) {
            Timber.tag(TAG).d("entered sign up user function")
            val signUpUserBodyDto = signUpBodyDtoAndModelMapper.mapBtoA(signUpBodyModel)
            val signUpResponse = chatApi.signUpUser(signUpUserBodyDto = signUpUserBodyDto)
            val isSignUpSuccessful = signUpResponse.isSuccessful || signUpResponse.body() == null

            Timber.tag(TAG).d("sign up response is ${signUpResponse.body()} successful result is $isSignUpSuccessful")

            if (!isSignUpSuccessful) {
                throw UnSuccessfulSignUpException(
                    signUpResponse.errorBody()?.string()
                        ?: "unable to sign up user, try again later"
                )
            }

            val signUpResponseBody = signUpResponse.body() ?: throw UnSuccessfulSignUpException(
                signUpResponse.errorBody()?.string() ?: "unable to sign up user, try again later"
            )
            val signUpResponseModel =
                signUpUserResponseDtoAndModelMapper.mapAtoB(signUpResponseBody)

            signUpResponseModel
        }
    }
}