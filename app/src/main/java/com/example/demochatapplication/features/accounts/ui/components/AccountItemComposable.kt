package com.example.demochatapplication.features.accounts.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.demochatapplication.features.accounts.domain.model.AccountUserModel
import com.example.demochatapplication.features.authentication.ui.login.utils.PaddingValues

/**
 * Created by Jatin Vashisht on 25-10-2023.
 */

@Composable
fun AccountItemComposable(
    modifier: Modifier = Modifier,
    accountUserModel: AccountUserModel,
) {
    Column(modifier = modifier,) {
        Row(modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues.MEDIUM),
        ) {
            Text(text = accountUserModel.username, modifier = Modifier.align(Alignment.CenterVertically), color = Color.White)
        }
    }
}