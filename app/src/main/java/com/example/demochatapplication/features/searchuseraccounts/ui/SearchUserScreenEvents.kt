package com.example.demochatapplication.features.searchuseraccounts.ui

sealed class SearchUserScreenEvents {
    data class Navigate(val destination: String): SearchUserScreenEvents()
}
