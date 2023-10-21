package com.example.demochatapplication.features.accounts.ui.components

import com.example.demochatapplication.features.accounts.domain.model.UserModel

/**
 * Created by Jatin Vashisht on 20-10-2023.
 */
data class AccountsScreenState(
    val error: String = "",
    val loading: Boolean = false,
    val data: MutableList<UserModel> = mutableListOf(),
)

data class SearchUsersComponentState(
    val shouldShowComponent: Boolean = false,
    val loading: Boolean = false,
    val searchResult: MutableList<UserModel> = mutableListOf(),
)