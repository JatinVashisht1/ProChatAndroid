package com.example.demochatapplication.features.searchuseraccounts.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity("searchuserentity", indices = [Index(value = ["username"], unique = true)])
data class SearchUserDatabaseEntity(
    @ColumnInfo("username")
    val username: String,
    @PrimaryKey(autoGenerate = true)
    val primaryKey: Int? = null
)
