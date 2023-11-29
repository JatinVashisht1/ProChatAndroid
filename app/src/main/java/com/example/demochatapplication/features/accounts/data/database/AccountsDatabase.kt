package com.example.demochatapplication.features.accounts.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.demochatapplication.features.accounts.data.database.dao.AccountUserDao
import com.example.demochatapplication.features.accounts.data.database.entities.AccountsUserEntity

/**
 * Created by Jatin Vashisht on 17-10-2023.
 */

@Database(entities = [AccountsUserEntity::class], version = 1)
abstract class AccountsDatabase: RoomDatabase() {
    abstract val accountUserDao: AccountUserDao

    companion object {
        const val SEARCH_USER_DATABASE_NAME = "SEARCH_USER_DATABASE"
    }
}