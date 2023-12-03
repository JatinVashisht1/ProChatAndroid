package com.example.demochatapplication.features.accounts.ui

/**
 * Created by Jatin Vashisht on 20-10-2023.
 */

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.demochatapplication.core.navigation.Destinations
import com.example.demochatapplication.features.accounts.domain.model.AccountUserModel
import com.example.demochatapplication.features.accounts.domain.repository.AccountsUserRepository
import com.example.demochatapplication.features.accounts.ui.components.AccountsScreenState
import com.example.demochatapplication.features.accounts.ui.components.SearchUsersComponentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel()
class AccountsScreenViewModel @Inject constructor(
    private val accountsUserRepository: AccountsUserRepository,
) : ViewModel() {
    private var getAllUserAccountsJob: Job? = null
    val accountScreenEvents = Channel<AccountScreenEvents>()

    private val _accountsUserModelPagingDataFlow =
        MutableStateFlow<PagingData<AccountUserModel>>(PagingData.empty())
    val accountsUserModelPagingDataFlow: StateFlow<PagingData<AccountUserModel>> get() = _accountsUserModelPagingDataFlow.asStateFlow()

    init {
        viewModelScope.launch {
            getAllUserAccountsJob?.cancel()
            getAllUserAccountsJob = launch {
                accountsUserRepository
                    .getAllUsers()
                    .cachedIn(viewModelScope)
                    .collectLatest { accountsUserModelPagingData ->
                        _accountsUserModelPagingDataFlow.value = accountsUserModelPagingData
                    }
            }
        }
    }

    private suspend fun sendEvent(event: AccountScreenEvents) {
        accountScreenEvents.send(event)
    }

    fun onChatAccountItemClicked(anotherUsername: String) {
        viewModelScope.launch {
            val destination = Destinations.ChatScreen.route + "/$anotherUsername"
            sendEvent(AccountScreenEvents.NavigateTo(destination = destination))
        }
    }

    override fun onCleared() {
        super.onCleared()
        getAllUserAccountsJob?.cancel()
    }
}

sealed interface AccountScreenEvents {
    data class NavigateTo(val destination: String) : AccountScreenEvents
}