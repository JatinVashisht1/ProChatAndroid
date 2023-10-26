package com.example.demochatapplication.features.accounts.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Jatin Vashisht on 17-10-2023.
 */

@Entity("accountsusertable")
data class AccountsUserEntity(
    val username: String,
    @PrimaryKey(autoGenerate = true)
    val primaryId: Int?,
)
