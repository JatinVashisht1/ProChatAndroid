package com.example.demochatapplication.features.destinationswitcher.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.demochatapplication.features.login.ui.utils.PaddingValues

@Composable
fun ErrorComposable(
    modifier: Modifier = Modifier,
    reason: String,
    onRetryClicked: () -> Unit
) {
    Column(modifier = modifier) {
        Text(text = reason)
        Spacer(modifier = Modifier.height(PaddingValues.LARGE))
        Button(onClick = onRetryClicked) {
            Text(text = "Retry")
        }

    }
}