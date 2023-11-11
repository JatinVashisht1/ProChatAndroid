package com.example.demochatapplication.features.accounts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.demochatapplication.core.Resource
import com.example.demochatapplication.features.accounts.ui.components.AccountsScreenComposable
import com.example.demochatapplication.features.accounts.ui.components.ErrorComposable
import com.example.demochatapplication.features.accounts.ui.components.LoadingComposable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Created by Jatin Vashisht on 20-10-2023.
 */

@Composable
fun AccountsScreenParent(
    accountsScreenViewModel: AccountsScreenViewModel = hiltViewModel(),
    navHostController: NavHostController,
) {
    val userState = accountsScreenViewModel.userAccountState.collectAsState().value
    val lazyListState = rememberLazyListState()

    LaunchedEffect(key1 = Unit) {
        accountsScreenViewModel.accountScreenEvents.receiveAsFlow().collectLatest { event ->
            when (event) {
                is AccountScreenEvents.NavigateTo -> {
                    navHostController.navigate(event.destination)
                }
            }
        }
    }

    when (userState) {
        is Resource.Error -> {
            ErrorComposable(
                modifier = Modifier.fillMaxSize(),
                error = userState.error,
            )
        }

        is Resource.Loading -> {
            LoadingComposable(modifier = Modifier.fillMaxSize())
        }

        is Resource.Success -> {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.surface) {
                AccountsScreenComposable(
                    accounts = userState.result ?: emptyList(),
                    modifier = Modifier
                        .fillMaxSize(),
                    lazyColumnState = lazyListState
                ) { username ->
                    accountsScreenViewModel.onChatAccountItemClicked(username)
                }
            }
        }
    }
}
