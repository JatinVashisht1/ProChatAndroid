package com.example.demochatapplication.features.login.data.repository

import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.features.login.core.UnsuccessfulLoginException
import com.example.demochatapplication.features.login.data.dto.SignInBodyDto
import com.example.demochatapplication.features.login.data.dto.SignInResponseDto
import com.example.demochatapplication.features.login.data.mapper.SignInDtoAndEntityMapper
import com.example.demochatapplication.features.login.data.mapper.SignInResponseDtoAndEntityMapper
import com.example.demochatapplication.features.login.data.remote.ChatApi
import com.example.demochatapplication.features.login.domain.model.SignInBodyEntity
import com.example.demochatapplication.features.login.domain.model.SignInResponseEntity
import com.example.demochatapplication.features.login.domain.repository.IAuthenticationRepository
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val chatApi: ChatApi,
    private val signInDtoAndEntityMapper: Mapper<SignInBodyDto, SignInBodyEntity>,
    private val signInResponseDtoAndEntityMapper: Mapper<SignInResponseDto, SignInResponseEntity>,
): IAuthenticationRepository {

    @kotlin.jvm.Throws(UnsuccessfulLoginException::class)
    override suspend fun signInUser(signInBodyEntity: SignInBodyEntity): SignInResponseEntity {
        val signInBodyDto = signInDtoAndEntityMapper.mapBtoA(signInBodyEntity);
        val loginUserResponse = chatApi.signInUser(signInBodyDto);

        if (!loginUserResponse.isSuccessful) {
            throw UnsuccessfulLoginException(loginUserResponse.message())
        }

        val signInResponseDto = loginUserResponse.body()
            ?: throw UnsuccessfulLoginException("Empty response body.")

        val signInResponseEntity = signInResponseDtoAndEntityMapper.mapAtoB(signInResponseDto)

        return signInResponseEntity;
    }
}