package com.example.demochatapplication.features.chat.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.demochatapplication.features.chat.ui.uistate.SendMessageTextFieldState

@Composable
fun SendMessageTextField(
    textFieldState: SendMessageTextFieldState,
    onTypingMessageValueChange: (String) -> Unit,
) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = textFieldState.message,
            onValueChange = onTypingMessageValueChange,
            placeholder = { Text(text = textFieldState.placeholderValue) },
            label = { Text(text = textFieldState.label) }
        )
}