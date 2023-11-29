package com.example.demochatapplication.features.searchuseraccounts.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity("searchuserentity", indices = [Index(value = ["username"], unique = true)])
data class SearchUserDatabaseEntity(
    @ColumnInfo("username")
    val username: String,
    val primaryKey: Int? = null
)
