package com.example.demochatapplication.features.login.domain.repository

import com.example.demochatapplication.features.login.domain.model.SignInBodyEntity
import com.example.demochatapplication.features.login.domain.model.SignInResponseEntity

interface IAuthenticationRepository {
    suspend fun signInUser(signInBodyEntity: SignInBodyEntity): SignInResponseEntity
}