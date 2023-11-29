package com.example.demochatapplication.features.accounts.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.demochatapplication.features.accounts.domain.model.UserModel
import com.example.demochatapplication.features.login.ui.utils.PaddingValues

/**
 * Created by Jatin Vashisht on 25-10-2023.
 */

@Composable
fun AccountItemComposable(
    modifier: Modifier = Modifier,
    userModel: UserModel,
) {
    Column(modifier = modifier,) {
        Row(modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues.MEDIUM),
        ) {
            Text(text = userModel.username, modifier = Modifier.align(Alignment.CenterVertically), )
        }
    }
}