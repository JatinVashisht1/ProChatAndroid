package com.example.demochatapplication.features.searchuseraccounts.di

import android.app.Application
import androidx.room.Room
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.core.remote.ChatApi
import com.example.demochatapplication.features.searchuseraccounts.data.database.SearchUserDatabase
import com.example.demochatapplication.features.searchuseraccounts.data.database.entity.SearchUserDatabaseEntity
import com.example.demochatapplication.features.searchuseraccounts.data.mapper.SearchUserDbEntityAndModelMapper
import com.example.demochatapplication.features.searchuseraccounts.data.repository.SearchUserRepoImpl
import com.example.demochatapplication.features.searchuseraccounts.domain.model.SearchUserDomainModel
import com.example.demochatapplication.features.searchuseraccounts.domain.repository.SearchUserRepository
import com.example.demochatapplication.features.shared.internetconnectivity.NetworkConnectionManager
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchUserAccountsDependencyModule {
    @Provides
    @Singleton
    fun providesSearchUserDatabase(application: Application): SearchUserDatabase =
        Room
            .databaseBuilder(application, SearchUserDatabase::class.java, SearchUserDatabase.SEARCH_USER_DATABASE_NAME)
            .build()

    @Provides
    @Singleton
    fun providesSearchUserDbEntityAndModelMapper(): Mapper<SearchUserDatabaseEntity, SearchUserDomainModel> =
        SearchUserDbEntityAndModelMapper()

    @Provides
    @Singleton
    fun providesSearchUserRepository(
        searchUserDatabase: SearchUserDatabase,
        userSettingsRepository: UserSettingsRepository,
        chatApi: ChatApi,
        searchUserDbEntityAndModelMapper: Mapper<SearchUserDatabaseEntity, SearchUserDomainModel>,
        networkConnectionManager: NetworkConnectionManager,
    ): SearchUserRepository = SearchUserRepoImpl(
        searchUserDatabase = searchUserDatabase,
        userSettingsRepository = userSettingsRepository,
        chatApi = chatApi,
        searchUserDbEntityAndModelMapper = searchUserDbEntityAndModelMapper,
        networkConnectionManager = networkConnectionManager,
    )
}