package com.example.demochatapplication.features.shared.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.demochatapplication.features.authentication.ui.login.utils.PaddingValues

/**
 * Created by Jatin Vashisht on 25-10-2023.
 */

@Composable
fun ErrorComposable(
    modifier: Modifier = Modifier,
    error: String,
    shouldShowRetryButton: Boolean = false,
    onRetryButtonClicked: () -> Unit = {},
) {
    Column(modifier = modifier) {
        Text(
            text = error,
            color = MaterialTheme.colors.error,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
        Spacer(modifier = Modifier.height(PaddingValues.MEDIUM))

        if (shouldShowRetryButton) {
            Button(onClick = onRetryButtonClicked) {
                Text(text = "Retry")
            }
        }
    }
}