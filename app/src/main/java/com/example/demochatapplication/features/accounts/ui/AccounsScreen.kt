package com.example.demochatapplication.features.accounts.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Created by Jatin Vashisht on 20-10-2023.
 */

@Composable
fun AccountsScreenParent(accountsScreenViewModel: AccountsScreenViewModel = hiltViewModel()) {
    val screenState = accountsScreenViewModel.accountsScreenState.collectAsState()

}

@Composable
fun AccountsScreen() {

}