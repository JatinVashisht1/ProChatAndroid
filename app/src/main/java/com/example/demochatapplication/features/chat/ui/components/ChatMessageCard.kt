package com.example.demochatapplication.features.chat.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.demochatapplication.features.chat.domain.model.ChatModel
import com.example.demochatapplication.features.chat.ui.utils.CornerRoundnessDpValues
import com.example.demochatapplication.features.login.ui.utils.PaddingValues
import com.example.demochatapplication.ui.theme.DarkMessageCardBackgroundSender
import com.example.demochatapplication.ui.theme.Typography
import kotlin.math.absoluteValue

@Composable
fun ChatMessageCard(
    modifier: Modifier = Modifier,
    chatModel: ChatModel,
    cornerRoundnessDpValues: CornerRoundnessDpValues = CornerRoundnessDpValues().getSimpleRoundedCornerValues(),
    senderBackgroundColor: Color = MaterialTheme.colors.primary,
    receiverBackgroundColor: Color = DarkMessageCardBackgroundSender,
    username: String,
) {

    val maxWidth = LocalConfiguration.current.screenWidthDp
    Box(modifier = modifier){
        Card(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .wrapContentHeight()
                .align(if(username == chatModel.from) Alignment.CenterEnd else Alignment.CenterStart),
            backgroundColor = if (username == chatModel.from) senderBackgroundColor else receiverBackgroundColor,
            shape = RoundedCornerShape(
                topStart = cornerRoundnessDpValues.topStart,
                topEnd = cornerRoundnessDpValues.topEnd,
                bottomStart = cornerRoundnessDpValues.bottomStart,
                bottomEnd = cornerRoundnessDpValues.bottomEnd
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues.MEDIUM),
                horizontalAlignment = if (chatModel.from == username) Alignment.End else Alignment.Start,
            ) {
                Text(
                    text = chatModel.message,
                    style = Typography.body1,
                    modifier = Modifier
                        .wrapContentWidth()
                        .widthIn(min = PaddingValues.VERY_SMALL, (maxWidth.absoluteValue * 0.75).dp)
//                    .padding(PaddingValues.VERY_SMALL)
                )
            }
        }
    }
}

@Composable
@Preview()
fun PreviewChatMessageCard() {
    ChatMessageCard(
        modifier = Modifier
            .padding(top = PaddingValues.MEDIUM)
            .fillMaxWidth()
            .wrapContentHeight(),
        chatModel = ChatModel(
            from = "def",
            to = "to",
            message = "this is message that no body fucking cares of motherfucker and this is something I am writing in order to check the preview",
//            message = ".",
        ),
        username = "abc",
        senderBackgroundColor = Color.Yellow
    )
}