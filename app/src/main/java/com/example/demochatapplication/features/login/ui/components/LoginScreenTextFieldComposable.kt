package com.example.demochatapplication.features.login.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.demochatapplication.core.CustomPaddingValues

@Composable
fun LoginScreenTextFieldComposable(
    value: String,
    modifier: Modifier = Modifier,
    placeholder: @Composable () -> Unit,
    label: @Composable () -> Unit,
    onValueChange: (newValue: String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = placeholder,
        label = label,
    )
}