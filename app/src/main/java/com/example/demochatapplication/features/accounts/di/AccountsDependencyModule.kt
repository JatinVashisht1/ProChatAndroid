package com.example.demochatapplication.features.accounts.di

import android.app.Application
import androidx.room.Room
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.features.accounts.data.database.AccountsDatabase
import com.example.demochatapplication.features.accounts.data.database.entities.AccountsUserEntity
import com.example.demochatapplication.features.accounts.data.mapper.AccountsUserEntityAndModelMapper
import com.example.demochatapplication.features.accounts.data.repository.AccountsScreenRepositoryImpl
import com.example.demochatapplication.features.accounts.domain.model.AccountUserModel
import com.example.demochatapplication.features.accounts.domain.repository.AccountsUserRepository
import com.example.demochatapplication.features.shared.internetconnectivity.NetworkConnectionManager
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
    fun providesSearchUserEntityAndModelMapper(): Mapper<AccountsUserEntity, AccountUserModel> =
        AccountsUserEntityAndModelMapper()

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
        accountsUserEntityAndModelMapper: Mapper<AccountsUserEntity, AccountUserModel>,
        chatApi: ChatApi,
        userSettingsRepository: UserSettingsRepository,
        networkConnectionManager: NetworkConnectionManager,
    ): AccountsUserRepository = AccountsScreenRepositoryImpl(
        accountsDatabase = accountsDatabase,
        accountsUserEntityAndModelMapper = accountsUserEntityAndModelMapper,
        chatApi = chatApi,
        userSettingsRepository = userSettingsRepository,
        networkConnectionManager = networkConnectionManager,
    )
}
