package com.example.demochatapplication.features.login.data.mapper

import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.features.login.data.dto.SignInBodyDto
import com.example.demochatapplication.features.login.domain.model.SignInBodyEntity
import javax.inject.Inject

class SignInDtoAndEntityMapper @Inject constructor() : Mapper<SignInBodyDto, SignInBodyEntity> {
    override fun mapAtoB(signInBodyDto: SignInBodyDto): SignInBodyEntity {
        val signInBodyEntity = SignInBodyEntity(signInBodyDto.username, signInBodyDto.password, signInBodyDto.firebaseToken)
        return signInBodyEntity
    }

    override fun mapBtoA(signInBodyEntity: SignInBodyEntity): SignInBodyDto {
        val signInBodyDto = SignInBodyDto(signInBodyEntity.username, signInBodyEntity.password, signInBodyEntity.firebaseRegistrationToken)
        return signInBodyDto
    }
}