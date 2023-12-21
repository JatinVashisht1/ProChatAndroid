package com.example.demochatapplication.features.chat.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.demochatapplication.features.chat.ui.utils.ChatScreenContentParams

@Composable
fun ChatTopBar(
    chatScreenContentParams: ChatScreenContentParams,
    onDeleteMessagesClicked: () -> Unit
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = { Text(text = chatScreenContentParams.chatScreenState.anotherUsername) },
        actions = {
            AnimatedVisibility(chatScreenContentParams.chatScreenState.isSelectionModeEnabled) {
                Text(text = "${chatScreenContentParams.chatScreenState.selectedMessages.size} selected")
            }

            if (chatScreenContentParams.chatScreenState.isSelectionModeEnabled) {
                IconButton(onClick = onDeleteMessagesClicked) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "delete messages",
                        tint = MaterialTheme.colors.primary
                    )
                }
            }
        }
    )
}
