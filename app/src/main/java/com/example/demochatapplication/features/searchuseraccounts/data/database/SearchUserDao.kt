package com.example.demochatapplication.features.searchuseraccounts.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.demochatapplication.features.searchuseraccounts.data.database.entity.SearchUserDatabaseEntity

@Dao
interface SearchUserDao {

    @Insert(entity = SearchUserDatabaseEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchUserEntity(searchUserDatabaseEntity: SearchUserDatabaseEntity)

    @Query("SELECT * FROM searchuserentity WHERE `username` LIKE '%' || :username || '%'" )
    suspend fun searchAccountByUsername(username: String): PagingSource<Int, SearchUserDatabaseEntity>

}