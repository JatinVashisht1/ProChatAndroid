package com.example.demochatapplication.features.searchuseraccounts.ui.components

sealed class SearchUserScreenState {
    data class Success(val searchTextFieldState: TextFieldState): SearchUserScreenState()
}

data class TextFieldState (
    val text: String = "",
    val label: String = "",
    val placeholder: String = "",
    val error: String = "",
)
