package com.example.demochatapplication.features.searchuseraccounts.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.demochatapplication.features.searchuseraccounts.data.database.entity.SearchUserDatabaseEntity

@Database(entities = [SearchUserDatabaseEntity::class], version = 1)
abstract class SearchUserDatabase : RoomDatabase() {
    abstract val searchUserDao: SearchUserDao

    companion object {
        const val SEARCH_USER_DATABASE_NAME = "searchuserdatabase"
    }
}