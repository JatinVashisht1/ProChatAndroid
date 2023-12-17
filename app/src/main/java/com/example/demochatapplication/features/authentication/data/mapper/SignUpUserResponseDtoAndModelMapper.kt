package com.example.demochatapplication.features.authentication.data.mapper

import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.remote.dto.SignUpUserResponseDto
import com.example.demochatapplication.features.authentication.domain.model.SignUpResponseModel

class SignUpUserResponseDtoAndModelMapper: Mapper<SignUpUserResponseDto, SignUpResponseModel> {
    override fun mapAtoB(objectTypeA: SignUpUserResponseDto): SignUpResponseModel {
        return SignUpResponseModel(
            success = objectTypeA.success,
            jwtToken = objectTypeA.token
        )
    }

    // this method is very unlikely to be used
    override fun mapBtoA(objectTypeB: SignUpResponseModel): SignUpUserResponseDto {
        return SignUpUserResponseDto(
            message = "",
            success = objectTypeB.success,
            token = objectTypeB.jwtToken
        )
    }
}