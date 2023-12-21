package com.example.demochatapplication.features.chat.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.example.demochatapplication.features.authentication.ui.login.utils.PaddingValues
import com.example.demochatapplication.features.chat.ui.uistate.ChatScreenState

@Composable
fun ChatInputSection(
    chatScreenState: ChatScreenState.Success,
    onTypingMessageValueChange: (String) -> Unit,
    onSendTextMessageButtonClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = PaddingValues.MEDIUM)
            .rotate(180f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SendMessageTextField(
            textFieldState = chatScreenState.sendTextMessageState,
            onTypingMessageValueChange = onTypingMessageValueChange,
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .heightIn(min = 40.dp)
        )

        Spacer(modifier = Modifier.width(PaddingValues.MEDIUM))

        FloatingActionButton(
            onClick = onSendTextMessageButtonClicked,
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape),
            backgroundColor = MaterialTheme.colors.primary
        ) {
            Icon(imageVector = Icons.Filled.Send, contentDescription = "Send Message")
        }
    }
}