package com.example.demochatapplication.dependencyinjection

import android.app.Application
import androidx.room.Room
import com.example.demochatapplication.core.Constants
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.features.login.data.dto.SignInBodyDto
import com.example.demochatapplication.features.login.data.dto.SignInResponseDto
import com.example.demochatapplication.features.login.data.mapper.SignInDtoAndEntityMapper
import com.example.demochatapplication.features.login.data.mapper.SignInResponseDtoAndEntityMapper
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.features.accounts.data.database.SearchUserDatabase
import com.example.demochatapplication.features.accounts.data.database.entities.SearchUserEntity
import com.example.demochatapplication.features.accounts.data.mapper.SearchUserEntityAndModelMapper
import com.example.demochatapplication.features.accounts.data.pagination.SearchUserPaginator
import com.example.demochatapplication.features.accounts.data.repository.SearchUserRepositoryImpl
import com.example.demochatapplication.features.accounts.domain.model.UserModel
import com.example.demochatapplication.features.accounts.domain.pagination.Paginator
import com.example.demochatapplication.features.accounts.domain.repository.SearchUserRepository
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
import javax.inject.Named
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
    fun providesSignInDtoAndEntityMapper(): Mapper<SignInBodyDto, SignInBodyEntity> =
        SignInDtoAndEntityMapper()

    @Provides()
    @Singleton()
    fun providesSignInResponseDtoAndEntityMapper(): Mapper<SignInResponseDto, SignInResponseEntity> =
        SignInResponseDtoAndEntityMapper()

    @Provides()
    fun providesSearchUserEntityAndModelMapper(): Mapper<SearchUserEntity, UserModel> =
        SearchUserEntityAndModelMapper()

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

    @Provides()
    @Singleton()
    fun providesSearchUsernameDatabase(app: Application): SearchUserDatabase =
        Room
            .databaseBuilder(
                app,
                SearchUserDatabase::class.java,
                SearchUserDatabase.SEARCH_USER_DATABASE_NAME
            )
            .build()

    @Provides()
    @Singleton()
    fun providesSearchUserRepository(
        searchUserDatabase: SearchUserDatabase,
        searchUserEntityAndModelMapper: Mapper<SearchUserEntity, UserModel>,
        chatApi: ChatApi,
    ): SearchUserRepository = SearchUserRepositoryImpl(
        searchUserDatabase = searchUserDatabase,
        searchUserEntityAndModelMapper = searchUserEntityAndModelMapper,
        chatApi = chatApi,
    )

    @Provides()
    @Singleton()
    @Named(Constants.SEARCH_USER_PAGINATOR)
    fun providesPaginator(searchUserRepository: SearchUserRepository): Paginator<List<UserModel>> =
        SearchUserPaginator(searchUserRepository = searchUserRepository)
}