package com.example.demochatapplication.features.authentication.domain.repository

import com.example.demochatapplication.features.authentication.domain.model.SignInBodyEntity
import com.example.demochatapplication.features.authentication.domain.model.SignInResponseEntity
import com.example.demochatapplication.features.authentication.domain.model.SignUpBodyModel
import com.example.demochatapplication.features.authentication.domain.model.SignUpResponseModel

interface IAuthenticationRepository {
    suspend fun signInUser(signInBodyEntity: SignInBodyEntity): SignInResponseEntity

    suspend fun signUpUser(signUpBodyModel: SignUpBodyModel): SignUpResponseModel
}