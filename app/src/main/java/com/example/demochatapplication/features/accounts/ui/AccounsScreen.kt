package com.example.demochatapplication.features.accounts.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.demochatapplication.features.accounts.ui.components.AccountsScreenComposable
import com.example.demochatapplication.features.shared.composables.ErrorComposable
import com.example.demochatapplication.features.shared.composables.LoadingComposable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Created by Jatin Vashisht on 20-10-2023.
 */

@Composable
fun AccountsScreenParent(
    accountsScreenViewModel: AccountsScreenViewModel = hiltViewModel(),
    navHostController: NavHostController,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
) {
    val accountsUserModelPagingData =
        accountsScreenViewModel.accountsUserModelPagingDataFlow.collectAsLazyPagingItems()
    val refreshState = accountsUserModelPagingData.loadState.refresh

    val lazyListState = rememberLazyListState()

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                accountsScreenViewModel.loadAccounts(shouldLoadFromNetwork = true)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(key1 = Unit) {
        accountsScreenViewModel.accountScreenEvents.receiveAsFlow().collectLatest { event ->
            when (event) {
                is AccountScreenEvents.NavigateTo -> {
                    navHostController.navigate(event.destination)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (refreshState) {

            is LoadState.Error -> {
                ErrorComposable(
                    modifier = Modifier.fillMaxSize(),
                    error = refreshState.error.localizedMessage ?: "unable to load user accounts"
                )
            }

            is LoadState.Loading,
            is LoadState.NotLoading -> {
                if ((refreshState is LoadState.Loading) && (accountsUserModelPagingData.itemCount == 0)) {
                    LoadingComposable(
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center)
                    )
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background
                    ) {
                        AccountsScreenComposable(
                            accounts = accountsUserModelPagingData,
                            modifier = Modifier
                                .fillMaxSize(),
                            lazyColumnState = lazyListState,
                            navHostController = navHostController
                        ) { username ->
                            accountsScreenViewModel.onChatAccountItemClicked(username)
                        }
                    }
                }
            }
        }
    }
}
