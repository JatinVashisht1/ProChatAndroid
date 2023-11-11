package com.example.demochatapplication.features.accounts.data.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Created by Jatin Vashisht on 17-10-2023.
 */

@Entity(tableName = "accountsusertable", indices = [Index(value = ["username"], unique = true)])
data class AccountsUserEntity(

    @ColumnInfo("username")
    val username: String,
    @PrimaryKey(autoGenerate = true)
    val primaryId: Int?,
)
