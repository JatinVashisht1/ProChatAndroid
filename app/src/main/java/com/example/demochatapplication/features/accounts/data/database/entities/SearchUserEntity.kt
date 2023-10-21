package com.example.demochatapplication.features.accounts.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Jatin Vashisht on 17-10-2023.
 */

@Entity("searchusertable")
data class SearchUserEntity(
    val username: String,
    @PrimaryKey(autoGenerate = true)
    val primaryId: Int?,
)
