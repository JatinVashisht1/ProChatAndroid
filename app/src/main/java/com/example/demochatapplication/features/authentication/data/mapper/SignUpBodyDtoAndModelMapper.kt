package com.example.demochatapplication.features.authentication.data.mapper

import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.remote.dto.SignUpUserBodyDto
import com.example.demochatapplication.features.authentication.domain.model.SignUpBodyModel
import javax.inject.Inject

class SignUpBodyDtoAndModelMapper @Inject constructor(): Mapper<SignUpUserBodyDto, SignUpBodyModel> {
    override fun mapAtoB(objectTypeA: SignUpUserBodyDto): SignUpBodyModel {
        return SignUpBodyModel(
            username = objectTypeA.username,
            password = objectTypeA.password,
            firebaseRegistrationToken = objectTypeA.firebaseToken
        )
    }

    override fun mapBtoA(objectTypeB: SignUpBodyModel): SignUpUserBodyDto {
        return SignUpUserBodyDto(
            username = objectTypeB.username,
            password = objectTypeB.password,
            firebaseToken = objectTypeB.firebaseRegistrationToken
        )
    }
}