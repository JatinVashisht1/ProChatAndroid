package com.example.demochatapplication.features.searchuseraccounts.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.demochatapplication.core.navigation.Destinations
import com.example.demochatapplication.features.searchuseraccounts.domain.model.SearchUserDomainModel
import com.example.demochatapplication.features.searchuseraccounts.domain.repository.SearchUserRepository
import com.example.demochatapplication.features.searchuseraccounts.ui.components.TextFieldState
import com.example.demochatapplication.features.shared.usersettings.UserSettings
import com.example.demochatapplication.features.shared.usersettings.UserSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchUserViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
    private val searchUserRepository: SearchUserRepository,
) : ViewModel() {
    private lateinit var userSettings: UserSettings
    private var getUserCredentialsJob: Job? = null

    private val _searchUsernameFlow: MutableStateFlow<PagingData<SearchUserDomainModel>> =
        MutableStateFlow(PagingData.empty())
    val searchUsernameFlow: StateFlow<PagingData<SearchUserDomainModel>> get() = _searchUsernameFlow.asStateFlow()
    private var searchUsernameJob: Job? = null

    private val _searchUserTextFieldState = MutableStateFlow<TextFieldState>(TextFieldState(label = "search user", placeholder = "type here"))
    val searchUserTextFieldState: StateFlow<TextFieldState> get() = _searchUserTextFieldState.asStateFlow()

    val uiEvents: Channel<SearchUserScreenEvents> = Channel()

    init {
        getUserCredentialsJob?.cancel()
        viewModelScope.launch {
            getUserCredentialsJob = launch {
                userSettings = userSettingsRepository.getFirstEntry()
            }
            getUserCredentialsJob?.join()

            searchUsernameJob?.cancel()
            searchUsernameJob = launch {
                searchUserRepository.searchUser("")
                    .cachedIn(viewModelScope)
                    .collectLatest {
                        _searchUsernameFlow.value = it
                    }
            }
        }
    }

    fun onSearchTextFieldValueChange(newQuery: String) {
        viewModelScope.launch {
            _searchUserTextFieldState.value = _searchUserTextFieldState.value.copy(text = newQuery)
        }
    }

    fun searchUsername() {
        viewModelScope.launch {
            searchUserRepository.searchUser(queryString = _searchUserTextFieldState.value.text)
                .cachedIn(viewModelScope)
                .collectLatest { searchUserDomainModel ->
                    _searchUsernameFlow.value = searchUserDomainModel
                }
        }
    }

    fun onUsernameItemClicked(username: String) {
        viewModelScope.launch {
            val destination = Destinations.ChatScreen.route + "/$username"
            uiEvents.send(SearchUserScreenEvents.Navigate(destination = destination))
        }
    }

    override fun onCleared() {
        super.onCleared()
        getUserCredentialsJob?.cancel()
        searchUsernameJob?.cancel()
    }

    companion object {
        const val TAG = "searchuserviewmodel"
    }
}

