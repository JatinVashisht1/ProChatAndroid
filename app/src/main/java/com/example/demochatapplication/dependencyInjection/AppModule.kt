package com.example.demochatapplication.dependencyinjection

import android.app.Application
import androidx.room.Room
import com.example.demochatapplication.core.Constants
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.core.remote.dto.GetChatMessagesBetween2UsersDto
import com.example.demochatapplication.features.accounts.data.database.AccountsDatabase
import com.example.demochatapplication.features.accounts.data.database.entities.AccountsUserEntity
import com.example.demochatapplication.features.accounts.data.mapper.SearchUserEntityAndModelMapper
import com.example.demochatapplication.features.accounts.data.repository.AccountsScreenRepositoryImpl
import com.example.demochatapplication.features.accounts.domain.model.UserModel
import com.example.demochatapplication.features.accounts.domain.repository.AccountsUserRepository
import com.example.demochatapplication.features.accounts.domain.usecase.ObserveAllUsersUseCase
import com.example.demochatapplication.features.chat.data.database.ChatDatabase
import com.example.demochatapplication.features.chat.data.database.entity.ChatDbEntity
import com.example.demochatapplication.features.chat.data.mapper.ChatDbEntityAndModelMapper
import com.example.demochatapplication.features.chat.data.mapper.ChatMessageDtoAndDbEntityMapper
import com.example.demochatapplication.features.chat.data.mapper.MessageDeliveryStateAndStringMapper
import com.example.demochatapplication.features.chat.data.repository.ChatRepositoryImpl
import com.example.demochatapplication.features.chat.domain.model.ChatScreenUiModel
import com.example.demochatapplication.features.chat.domain.model.MessageDeliveryState
import com.example.demochatapplication.features.chat.domain.repository.ChatRepository
import com.example.demochatapplication.features.login.data.dto.SignInBodyDto
import com.example.demochatapplication.features.login.data.dto.SignInResponseDto
import com.example.demochatapplication.features.login.data.mapper.SignInDtoAndEntityMapper
import com.example.demochatapplication.features.login.data.mapper.SignInResponseDtoAndEntityMapper
import com.example.demochatapplication.features.login.data.repository.AuthenticationRepositoryImpl
import com.example.demochatapplication.features.login.domain.model.SignInBodyEntity
import com.example.demochatapplication.features.login.domain.model.SignInResponseEntity
import com.example.demochatapplication.features.login.domain.repository.IAuthenticationRepository
import com.example.demochatapplication.features.searchuseraccounts.data.database.SearchUserDatabase
import com.example.demochatapplication.features.shared.cryptomanager.CryptoManager
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
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
    fun providesSignInDtoAndEntityMapper(): Mapper<SignInBodyDto, SignInBodyEntity> =
        SignInDtoAndEntityMapper()

    @Provides()
    @Singleton()
    fun providesSignInResponseDtoAndEntityMapper(): Mapper<SignInResponseDto, SignInResponseEntity> =
        SignInResponseDtoAndEntityMapper()

    @Provides()
    fun providesSearchUserEntityAndModelMapper(): Mapper<AccountsUserEntity, UserModel> =
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
        )

    @Provides()
    @Singleton()
    fun providesCryptoManager(): CryptoManager = CryptoManager()

    fun providesUserSettingsSerializer(cryptoManager: CryptoManager): UserSettingsSerializer =
        UserSettingsSerializer(cryptoManager = cryptoManager)

    @Provides()
    @Singleton()
    fun providesSearchUsernameDatabase(app: Application): AccountsDatabase =
        Room
            .databaseBuilder(
                app,
                AccountsDatabase::class.java,
                AccountsDatabase.SEARCH_USER_DATABASE_NAME
            )
            .build()

    @Provides()
    @Singleton()
    fun providesSearchUserRepository(
        accountsDatabase: AccountsDatabase,
        accountsUserEntityAndModelMapper: Mapper<AccountsUserEntity, UserModel>,
        chatApi: ChatApi,
        userSettingsRepository: UserSettingsRepository,
    ): AccountsUserRepository = AccountsScreenRepositoryImpl(
        accountsDatabase = accountsDatabase,
        accountsUserEntityAndModelMapper = accountsUserEntityAndModelMapper,
        chatApi = chatApi,
        userSettingsRepository = userSettingsRepository,
    )

    @Provides()
    @Singleton()
    fun providesObserveAllUsersUseCase(accountsUserRepository: AccountsUserRepository): ObserveAllUsersUseCase =
        ObserveAllUsersUseCase(accountsUserRepository = accountsUserRepository)


    @Provides()
    @Singleton()
    fun providesChatDatabase(app: Application): ChatDatabase =
        Room
            .databaseBuilder(app, ChatDatabase::class.java, ChatDatabase.CHAT_DB_NAME)
            .build()

    @Provides()
    @Singleton()
    fun providesChatDbEntityAndModelMapper(): Mapper<ChatDbEntity, ChatScreenUiModel.ChatModel> =
        ChatDbEntityAndModelMapper()


    @Provides()
    @Singleton()
    fun providesMessageDeliveryStateAndStringMapper(): Mapper<MessageDeliveryState, String> =
        MessageDeliveryStateAndStringMapper()

    @Provides()
    @Singleton()
    fun providesChatRepository(
        chatApi: ChatApi,
        chatDatabase: ChatDatabase,
        userSettingsRepository: UserSettingsRepository,
        chatDbEntityAndModelMapper: Mapper<ChatDbEntity, ChatScreenUiModel.ChatModel>,
        messageDeliveryStateAndStringMapper: Mapper<MessageDeliveryState, String>,
        chatMessageDtoAndDbEntityMapper: Mapper<GetChatMessagesBetween2UsersDto, List<ChatDbEntity>>,
    ): ChatRepository = ChatRepositoryImpl(
        chatApi = chatApi,
        chatDatabase = chatDatabase,
        userSettingsRepository = userSettingsRepository,
        chatDbEntityAndModelMapper = chatDbEntityAndModelMapper,
        messageDeliveryStateAndStringMapper = messageDeliveryStateAndStringMapper,
        chatMessageDtoAndDbEntityMapper = chatMessageDtoAndDbEntityMapper,
    )

    @Provides()
    @Singleton()
    fun providesGetChatMessageBetween2UsersDtoAndDbEntityMapper(
        messageDeliveryStateAndStringMapper: Mapper<MessageDeliveryState, String>
    ): Mapper<GetChatMessagesBetween2UsersDto, List<ChatDbEntity>> =
        ChatMessageDtoAndDbEntityMapper(messageDeliveryStateAndStringMapper = messageDeliveryStateAndStringMapper)

    @Provides()
    @Singleton()
    fun providesSearchUserDatabase(
        application: Application,
    ): SearchUserDatabase =
        Room
            .databaseBuilder(application, SearchUserDatabase::class.java, SearchUserDatabase.SEARCH_USER_DATABASE_NAME)
            .build()
}