package com.example.demochatapplication.features.searchuseraccounts.di

import android.app.Application
import androidx.room.Room
import com.example.demochatapplication.core.Mapper
import com.example.demochatapplication.features.searchuseraccounts.data.database.SearchUserDatabase
import com.example.demochatapplication.features.searchuseraccounts.data.database.entity.SearchUserDatabaseEntity
import com.example.demochatapplication.features.searchuseraccounts.data.mapper.SearchUserDbEntityAndModelMapper
import com.example.demochatapplication.features.searchuseraccounts.domain.model.SearchUserDomainModel
import dagger.Provides
import javax.inject.Singleton

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
}