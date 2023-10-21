package com.example.demochatapplication.features.accounts.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.example.demochatapplication.features.accounts.data.database.entities.SearchUserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Created by Jatin Vashisht on 17-10-2023.
 */

@Dao
interface SearchUserDao {
    @Query("SELECT * FROM searchusertable")
    suspend fun getAllUsers(): List<SearchUserEntity>

    @Query("SELECT * FROM searchusertable WHERE username LIKE '%' || :username || '%'")
    suspend fun searchUserFromDatabase(username: String): List<SearchUserEntity>

    @Query("DELETE from searchusertable WHERE username = :username")
    suspend fun deleteUsername(username: String)

    @Insert(onConflict = IGNORE)
    suspend fun insertUsers(vararg searchUserEntity: SearchUserEntity)
}