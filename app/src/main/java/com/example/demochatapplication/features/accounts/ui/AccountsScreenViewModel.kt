package com.example.demochatapplication.features.accounts.ui

/**
 * Created by Jatin Vashisht on 20-10-2023.
 */

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demochatapplication.core.Constants
import com.example.demochatapplication.core.Resource
import com.example.demochatapplication.features.accounts.domain.model.UserModel
import com.example.demochatapplication.features.accounts.domain.pagination.Paginator
import com.example.demochatapplication.features.accounts.domain.usecase.ObserveAllUsersUseCase
import com.example.demochatapplication.features.accounts.ui.components.AccountsScreenState
import com.example.demochatapplication.features.accounts.ui.components.SearchUsersComponentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel()
class AccountsScreenViewModel @Inject constructor(
    private val observeAllUsersUseCase: ObserveAllUsersUseCase,
) : ViewModel() {
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
            observeAllUsersUseCase()
        }
    }


}
