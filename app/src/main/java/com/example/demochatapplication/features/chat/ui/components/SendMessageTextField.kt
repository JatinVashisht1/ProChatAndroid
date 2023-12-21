package com.example.demochatapplication.features.chat.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.demochatapplication.features.chat.ui.uistate.SendMessageScreenTextFieldState

@Composable
fun SendMessageTextField(
    modifier: Modifier = Modifier,
    textFieldState: SendMessageScreenTextFieldState,
    onTypingMessageValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier,
        value = textFieldState.message,
        onValueChange = onTypingMessageValueChange,

        placeholder = { Text(text = textFieldState.placeholderValue) },
        label = { Text(text = textFieldState.label) },
        shape = RoundedCornerShape(100.dp)
    )
}