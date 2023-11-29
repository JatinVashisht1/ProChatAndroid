package com.example.demochatapplication.features.accounts.di

import android.app.Application
import androidx.room.Room
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.features.accounts.data.database.AccountsDatabase
import com.example.demochatapplication.features.accounts.data.database.entities.AccountsUserEntity
import com.example.demochatapplication.features.accounts.data.mapper.SearchUserEntityAndModelMapper
import com.example.demochatapplication.features.accounts.data.repository.AccountsScreenRepositoryImpl
import com.example.demochatapplication.features.accounts.domain.model.UserModel
import com.example.demochatapplication.features.accounts.domain.repository.AccountsUserRepository
import com.example.demochatapplication.features.accounts.domain.usecase.ObserveAllUsersUseCase
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AccountsModule {
    @Provides
    @Singleton
    fun providesSearchUserEntityAndModelMapper(): Mapper<AccountsUserEntity, UserModel> =
        SearchUserEntityAndModelMapper()

    @Provides
    @Singleton
    fun providesSearchUsernameDatabase(app: Application): AccountsDatabase =
        Room
            .databaseBuilder(
                app,
                AccountsDatabase::class.java,
                AccountsDatabase.SEARCH_USER_DATABASE_NAME
            )
            .build()

    @Provides
    @Singleton
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

    @Provides
    @Singleton
    fun providesObserveAllUsersUseCase(accountsUserRepository: AccountsUserRepository): ObserveAllUsersUseCase =
        ObserveAllUsersUseCase(accountsUserRepository = accountsUserRepository)
}
