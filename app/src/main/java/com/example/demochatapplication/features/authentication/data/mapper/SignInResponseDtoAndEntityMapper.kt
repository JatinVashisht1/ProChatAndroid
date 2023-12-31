package com.example.demochatapplication.features.authentication.data.mapper

import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.remote.dto.SignInResponseDto
import com.example.demochatapplication.features.authentication.domain.model.SignInResponseEntity
import javax.inject.Inject

class SignInResponseDtoAndEntityMapper @Inject constructor() : Mapper<SignInResponseDto, SignInResponseEntity> {
    override fun mapAtoB(signInResponseDto: SignInResponseDto): SignInResponseEntity {
        val signInResponseEntity = SignInResponseEntity(success = signInResponseDto.success, token = signInResponseDto.token)
        return signInResponseEntity
    }

    override fun mapBtoA(signInResponseEntity: SignInResponseEntity): SignInResponseDto {
        val signInResponseDto = SignInResponseDto(success = signInResponseEntity.success, token = signInResponseEntity.token)
        return signInResponseDto
    }
}