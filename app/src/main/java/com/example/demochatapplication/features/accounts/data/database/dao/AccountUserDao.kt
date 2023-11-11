package com.example.demochatapplication.features.accounts.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import com.example.demochatapplication.features.accounts.data.database.entities.AccountsUserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Jatin Vashisht on 17-10-2023.
 */

@Dao
interface AccountUserDao {
    @Query("SELECT * FROM accountsusertable")
    fun getAllUsers(): Flow<List<AccountsUserEntity>>

    @Query("SELECT * FROM accountsusertable WHERE username LIKE '%' || :username || '%'")
    suspend fun searchUserFromDatabase(username: String): List<AccountsUserEntity>

    @Query("DELETE from accountsusertable WHERE username = :username")
    suspend fun deleteUsername(username: String)

    @Insert(onConflict = IGNORE)
    suspend fun insertUsers(vararg accountsUserEntity: AccountsUserEntity)

    @Query("SELECT COUNT(*) FROM accountsusertable")
    suspend fun getUserAccountsCount(): Int
}