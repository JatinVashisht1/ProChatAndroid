package com.example.demochatapplication.features.accounts.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Created by Jatin Vashisht on 25-10-2023.
 */

@Composable
fun ErrorComposable(
    modifier: Modifier = Modifier,
    error: String,
) {
    Box(modifier = modifier) {
        Text(text = error, color = MaterialTheme.colors.error, modifier = Modifier.align(Alignment.Center))
    }
}