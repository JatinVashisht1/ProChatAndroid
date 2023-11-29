package com.example.demochatapplication.features.accounts.ui

/**
 * Created by Jatin Vashisht on 20-10-2023.
 */

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demochatapplication.core.Resource
import com.example.demochatapplication.features.accounts.domain.model.UserModel
import com.example.demochatapplication.features.accounts.domain.usecase.ObserveAllUsersUseCase
import com.example.demochatapplication.features.accounts.ui.components.AccountsScreenState
import com.example.demochatapplication.features.accounts.ui.components.SearchUsersComponentState
import com.example.demochatapplication.core.navigation.Destinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel()
class AccountsScreenViewModel @Inject constructor(
    private val observeAllUsersUseCase: ObserveAllUsersUseCase,
) : ViewModel() {
    val accountScreenEvents = Channel<AccountScreenEvents>()


    private val _accountsScreenState =
        MutableStateFlow<AccountsScreenState>(AccountsScreenState(loading = false))
    val accountsScreenState: StateFlow<AccountsScreenState> get() = _accountsScreenState.asStateFlow()

    private val _searchAccountComponentState = MutableStateFlow<SearchUsersComponentState>(
        SearchUsersComponentState()
    )
    val searchUserComponentState: StateFlow<SearchUsersComponentState> get() = _searchAccountComponentState.asStateFlow()

    private val _userAccountsState = observeAllUsersUseCase.userModelState
    val userAccountState: StateFlow<Resource<List<UserModel>>> = _userAccountsState.asStateFlow()

    init {
        viewModelScope.launch {
            observeAllUsersUseCase(shouldLoadFromNetwork = false)
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
}

sealed interface AccountScreenEvents {
    data class NavigateTo(val destination: String): AccountScreenEvents
}