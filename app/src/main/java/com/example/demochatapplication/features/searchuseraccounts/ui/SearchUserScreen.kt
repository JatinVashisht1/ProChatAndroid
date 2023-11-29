package com.example.demochatapplication.features.searchuseraccounts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.demochatapplication.features.login.ui.utils.PaddingValues
import com.example.demochatapplication.features.searchuseraccounts.domain.model.SearchUserDomainModel
import com.example.demochatapplication.features.searchuseraccounts.ui.components.TextFieldState
import com.example.demochatapplication.features.shared.composables.ErrorComposable
import com.example.demochatapplication.features.shared.composables.LoadingComposable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber

@Composable
fun SearchUserScreenParent(
    searchUserViewModel: SearchUserViewModel = hiltViewModel(),
    navHostController: NavHostController,
) {
    val usernamePagingState = searchUserViewModel.searchUsernameFlow.collectAsLazyPagingItems()
    val searchTextFieldState = searchUserViewModel.searchUserTextFieldState.collectAsState()
    val lazyListState = rememberLazyListState()

    LaunchedEffect(key1 = Unit) {
        searchUserViewModel.uiEvents.receiveAsFlow().collectLatest { uiEvent->
            when(uiEvent) {
                is SearchUserScreenEvents.Navigate -> {
                    navHostController.navigate(uiEvent.destination)
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = searchTextFieldState.value.text,
                onValueChange = searchUserViewModel::onTextFieldValueChange,
                label = {
                    Text(
                        text = searchTextFieldState.value.text
                    )
                },
                placeholder = {
                    Text(text = searchTextFieldState.value.placeholder)
                })

            Spacer(modifier = Modifier.width(PaddingValues.MEDIUM))

            IconButton(onClick = searchUserViewModel::searchUsername) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "search user",
                    tint = MaterialTheme.colors.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(PaddingValues.MEDIUM))

        when (usernamePagingState.loadState.refresh) {
            is LoadState.Error -> {
                ErrorComposable(error = "unable to load data")
            }

            LoadState.Loading -> {
                LoadingComposable(
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            is LoadState.NotLoading -> {
                SearchUserScreen(
                    usernamesPagingData = usernamePagingState,
                    modifier = Modifier.fillMaxSize(),
                    lazyListState = lazyListState,
                    searchTextFieldState = searchTextFieldState.value,
                    onSearchTextFieldValueChange = searchUserViewModel::onTextFieldValueChange,
                    onSearchUserButtonClicked = searchUserViewModel::searchUsername,
                    onSearchUserItemClicked = searchUserViewModel::onUsernameItemClicked
                )
            }
        }
    }

}

@Composable
fun SearchUserScreen(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    usernamesPagingData: LazyPagingItems<SearchUserDomainModel>,
    searchTextFieldState: TextFieldState,
    onSearchTextFieldValueChange: (String) -> Unit,
    onSearchUserButtonClicked: () -> Unit,
    onSearchUserItemClicked: (String) -> Unit,
) {
    LazyColumn(modifier = modifier, state = lazyListState) {
        val usernameCount = usernamesPagingData.itemCount
        items(usernameCount) { index ->
            val usernameModel = usernamesPagingData[index]
            SideEffect {
                Timber.tag(TAG)
                    .d("usernamemodel: ${usernameModel?.username} username count: $usernameCount")
            }
            usernameModel?.let { searchUserDomainModel ->
                Spacer(modifier = Modifier.height(PaddingValues.MEDIUM))

                SearchUserItem(
                    searchUserDomainModel = searchUserDomainModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(horizontal = PaddingValues.MEDIUM)
                        .clip(RoundedCornerShape(PaddingValues.MEDIUM))
                        .background(color = Color(51, 20, 30, 255))
                        .clickable {
                            onSearchUserItemClicked(searchUserDomainModel.username)
                        }
                )

            }
        }
    }
}

@Composable
fun SearchUserItem(
    modifier: Modifier = Modifier,
    searchUserDomainModel: SearchUserDomainModel,
) {
    Box(modifier = modifier) {
        Text(
            text = searchUserDomainModel.username,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(PaddingValues.MEDIUM),
            color = MaterialTheme.colors.onBackground,
        )
    }
}

private const val TAG = "searchuserscreen"