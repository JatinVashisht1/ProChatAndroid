package com.example.demochatapplication.features.chat.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.demochatapplication.features.chat.domain.model.ChatModel
import com.example.demochatapplication.features.chat.ui.utils.CornerRoundnessDpValues
import com.example.demochatapplication.features.login.ui.utils.PaddingValues
import com.example.demochatapplication.ui.theme.Typography

@Composable
fun ChatMessageCard(
    modifier: Modifier = Modifier,
    chatModel: ChatModel,
    cornerRoundnessDpValues: CornerRoundnessDpValues = CornerRoundnessDpValues(),
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(
            topStart = cornerRoundnessDpValues.topStart,
            topEnd = cornerRoundnessDpValues.topEnd,
            bottomStart = cornerRoundnessDpValues.bottomStart,
            bottomEnd = cornerRoundnessDpValues.bottomEnd
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues.MEDIUM)
        ) {
            Text(
                text = chatModel.message,
                style = Typography.body1,
                modifier = Modifier
                    .padding(PaddingValues.SMALL)
            )
        }
    }
}

//@Composable
//@Preview()
//fun PreviewChatMessageCard() {
//    ChatMessageCard(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(100.dp),
//        chatModel = ChatModel(
//            from = "def",
//            to = "to",
//            message = "this is message that no body fucking cares of motherfucker"
//        )
//    )
//}