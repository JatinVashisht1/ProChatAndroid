package com.example.demochatapplication.features.accounts.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.demochatapplication.features.accounts.data.database.dao.SearchUserDao
import com.example.demochatapplication.features.accounts.data.database.entities.SearchUserEntity

/**
 * Created by Jatin Vashisht on 17-10-2023.
 */

@Database(entities = [SearchUserEntity::class], version = 1)
abstract class SearchUserDatabase: RoomDatabase() {
    abstract val searchUserDao: SearchUserDao

    companion object {
        const val SEARCH_USER_DATABASE_NAME = "SEARCH_USER_DATABASE"
    }
}