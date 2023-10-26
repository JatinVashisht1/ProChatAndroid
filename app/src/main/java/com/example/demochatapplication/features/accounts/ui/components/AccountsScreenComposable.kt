package com.example.demochatapplication.features.accounts.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.demochatapplication.features.accounts.domain.model.UserModel
import com.example.demochatapplication.features.login.ui.utils.PaddingValues as MyPaddingValues

/**
 * Created by Jatin Vashisht on 25-10-2023.
 */

@Composable
fun AccountsScreenComposable(
    modifier: Modifier = Modifier,
    accounts: List<UserModel>,
    lazyColumnState: LazyListState = rememberLazyListState()
) {
    LazyColumn(
        modifier = modifier,
        state = lazyColumnState,
        contentPadding = PaddingValues(all = MyPaddingValues.MEDIUM)
    ) {
        items(accounts) {userModel->
            AccountItemComposable(
                userModel = userModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(color = MaterialTheme.colors.background)
            )
        }
    }
}