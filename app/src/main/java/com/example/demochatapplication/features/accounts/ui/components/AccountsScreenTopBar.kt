package com.example.demochatapplication.features.accounts.ui.components

import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.demochatapplication.core.navigation.Destinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsScreenTopBar(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
) {
    CenterAlignedTopAppBar(
        title = { Text(text = "Accounts", color = MaterialTheme.colors.onBackground) },
        navigationIcon = {
            IconButton(onClick = {
                navHostController.navigate(Destinations.SearchUserScreen.route)
            }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "search user", tint = MaterialTheme.colors.onBackground)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colors.background)
    )
}