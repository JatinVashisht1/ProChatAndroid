package com.example.demochatapplication.features.accounts.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import androidx.room.Transaction
import com.example.demochatapplication.features.accounts.data.database.entities.AccountsUserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Jatin Vashisht on 17-10-2023.
 */

@Dao
interface AccountUserDao {
    @Query("SELECT * FROM accountsusertable")
    fun getAllUsers(): PagingSource<Int, AccountsUserEntity>

    @Query("DELETE from accountsusertable WHERE username = :username")
    suspend fun deleteUsername(username: String)

    @Query("DELETE FROM accountsusertable")
    suspend fun deleteAllUsernames()

    @Insert(onConflict = IGNORE)
    suspend fun insertUsers(vararg accountsUserEntity: AccountsUserEntity)

    @Insert(onConflict = IGNORE)
    suspend fun insertUsers(accountsUserEntity: AccountsUserEntity)

    @Insert(onConflict = IGNORE)
    suspend fun insertUsers(accountsUserEntityList: List<AccountsUserEntity>)

    @Query("SELECT COUNT(*) FROM accountsusertable")
    suspend fun getUserAccountsCount(): Int

}