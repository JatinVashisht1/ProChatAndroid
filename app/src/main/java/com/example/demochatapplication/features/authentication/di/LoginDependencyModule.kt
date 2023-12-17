package com.example.demochatapplication.features.authentication.di

import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.core.remote.dto.SignInBodyDto
import com.example.demochatapplication.core.remote.dto.SignInResponseDto
import com.example.demochatapplication.core.remote.dto.SignUpUserBodyDto
import com.example.demochatapplication.core.remote.dto.SignUpUserResponseDto
import com.example.demochatapplication.features.authentication.data.mapper.SignInDtoAndEntityMapper
import com.example.demochatapplication.features.authentication.data.mapper.SignInResponseDtoAndEntityMapper
import com.example.demochatapplication.features.authentication.data.mapper.SignUpBodyDtoAndModelMapper
import com.example.demochatapplication.features.authentication.data.mapper.SignUpUserResponseDtoAndModelMapper
import com.example.demochatapplication.features.authentication.data.repository.AuthenticationRepositoryImpl
import com.example.demochatapplication.features.authentication.domain.model.SignInBodyEntity
import com.example.demochatapplication.features.authentication.domain.model.SignInResponseEntity
import com.example.demochatapplication.features.authentication.domain.model.SignUpBodyModel
import com.example.demochatapplication.features.authentication.domain.model.SignUpResponseModel
import com.example.demochatapplication.features.authentication.domain.repository.IAuthenticationRepository
import com.example.demochatapplication.features.shared.cryptomanager.CryptoManager
import com.example.demochatapplication.features.shared.usersettings.UserSettingsSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginDependencyModule {
    @Provides
    @Singleton
    fun providesSignInDtoAndEntityMapper(): Mapper<SignInBodyDto, SignInBodyEntity> =
        SignInDtoAndEntityMapper()

    @Provides
    @Singleton
    fun providesSignInResponseDtoAndEntityMapper(): Mapper<SignInResponseDto, SignInResponseEntity> =
        SignInResponseDtoAndEntityMapper()

    @Provides
    @Singleton
    fun providesAuthenticationRepository(
        chatApi: ChatApi,
        signInDtoAndEntityMapper: Mapper<SignInBodyDto, SignInBodyEntity>,
        signInResponseDtoAndEntityMapper: Mapper<SignInResponseDto, SignInResponseEntity>,
        signUpUserBodyDtoAndModelMapper: Mapper<SignUpUserBodyDto, SignUpBodyModel>,
        signUpUserResponseDtoAndModelMapper: Mapper<SignUpUserResponseDto, SignUpResponseModel>,
    ): IAuthenticationRepository =
        AuthenticationRepositoryImpl(
            chatApi = chatApi,
            signInDtoAndEntityMapper = signInDtoAndEntityMapper,
            signInResponseDtoAndEntityMapper = signInResponseDtoAndEntityMapper,
            signUpBodyDtoAndModelMapper = signUpUserBodyDtoAndModelMapper,
            signUpUserResponseDtoAndModelMapper = signUpUserResponseDtoAndModelMapper,
        )

    @Provides
    @Singleton
    fun providesCryptoManager(): CryptoManager = CryptoManager()

    @Provides
    @Singleton
    fun providesUserSettingsSerializer(cryptoManager: CryptoManager): UserSettingsSerializer =
        UserSettingsSerializer(cryptoManager = cryptoManager)

    @Provides
    @Singleton
    fun providesSignUpBodyDtoAndModelMapper(): Mapper<SignUpUserBodyDto, SignUpBodyModel> = SignUpBodyDtoAndModelMapper()

    @Provides
    @Singleton
    fun providesSignUpResponseDtoAndModelMapper(): Mapper<SignUpUserResponseDto, SignUpResponseModel> = SignUpUserResponseDtoAndModelMapper()
}