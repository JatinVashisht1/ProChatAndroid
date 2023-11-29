package com.example.demochatapplication.features.accounts.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.demochatapplication.features.accounts.domain.model.UserModel
import com.example.demochatapplication.features.login.ui.utils.PaddingValues

/**
 * Created by Jatin Vashisht on 25-10-2023.
 */

@Composable
fun AccountsScreenComposable(
    modifier: Modifier = Modifier,
    accounts: List<UserModel>,
    lazyColumnState: LazyListState = rememberLazyListState(),
    navHostController: NavHostController,
    onChatAccountClicked: (String) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = { AccountsScreenTopBar(navHostController = navHostController)}
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = lazyColumnState,
            contentPadding = paddingValues,
        ) {
            items(accounts) { userModel ->
                Spacer(modifier = Modifier.height(PaddingValues.MEDIUM))

                AccountItemComposable(
                    userModel = userModel,
                    modifier = Modifier
                        .padding(horizontal = PaddingValues.MEDIUM)
                        .clip(RoundedCornerShape(PaddingValues.MEDIUM))
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(color = Color(51, 20, 30, 255))
                        .clickable {
                            onChatAccountClicked(userModel.username)
                        }
                )

            }
        }
    }
}