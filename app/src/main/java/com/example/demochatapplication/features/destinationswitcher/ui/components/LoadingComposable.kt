package com.example.demochatapplication.features.destinationswitcher.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoadingComposable(modifier: Modifier = Modifier) {
    Column (modifier = modifier){
        Text(text = "Loading...")
    }
}