package com.example.demochatapplication.dependencyInjection

import com.example.demochatapplication.core.Constants
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.features.login.data.dto.SignInBodyDto
import com.example.demochatapplication.features.login.data.dto.SignInResponseDto
import com.example.demochatapplication.features.login.data.mapper.SignInDtoAndEntityMapper
import com.example.demochatapplication.features.login.data.mapper.SignInResponseDtoAndEntityMapper
import com.example.demochatapplication.features.login.data.remote.ChatApi
import com.example.demochatapplication.features.login.data.repository.AuthenticationRepositoryImpl
import com.example.demochatapplication.features.login.domain.model.SignInBodyEntity
import com.example.demochatapplication.features.login.domain.model.SignInResponseEntity
import com.example.demochatapplication.features.login.domain.repository.IAuthenticationRepository
import com.example.demochatapplication.features.shared.cryptomanager.CryptoManager
import com.example.demochatapplication.features.shared.usersettings.UserSettingsSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides()
    @Singleton()
    fun providesChatApi(): ChatApi = Retrofit
        .Builder()
        .baseUrl(Constants.SERVER_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ChatApi::class.java)

    @Provides()
    @Singleton()
//    @Named(Constants.SIGN_IN_DTO_AND_ENTITY_MAPPER)
    fun providesSignInDtoAndEntityMapper(): Mapper<SignInBodyDto, SignInBodyEntity> =
        SignInDtoAndEntityMapper()

    @Provides()
    @Singleton()
//    @Named(Constants.SIGN_IN_RESPONSE_AND_DTO_ENTITY_MAPPER)
    fun providesSignInResponseDtoAndEntityMapper(): Mapper<SignInResponseDto, SignInResponseEntity> =
        SignInResponseDtoAndEntityMapper()

    @Provides()
    @Singleton()
    fun providesAuthenticationRepository(
        chatApi: ChatApi,
        signInDtoAndEntityMapper: Mapper<SignInBodyDto, SignInBodyEntity>,
        signInResponseDtoAndEntityMapper: Mapper<SignInResponseDto, SignInResponseEntity>,
    )
            : IAuthenticationRepository =
        AuthenticationRepositoryImpl(
            chatApi = chatApi,
            signInDtoAndEntityMapper = signInDtoAndEntityMapper,
            signInResponseDtoAndEntityMapper = signInResponseDtoAndEntityMapper
        );

    @Provides()
    @Singleton()
    fun providesCryptoManager(): CryptoManager = CryptoManager()

    fun providesUserSettingsSerializer(cryptoManager: CryptoManager): UserSettingsSerializer =
        UserSettingsSerializer(cryptoManager = cryptoManager)

}