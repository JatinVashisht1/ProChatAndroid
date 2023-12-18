package com.example.demochatapplication.features.chat.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.example.demochatapplication.features.authentication.ui.login.utils.PaddingValues
import com.example.demochatapplication.features.chat.domain.model.ChatScreenUiModel
import com.example.demochatapplication.features.chat.ui.utils.CornerRoundnessDpValues
import com.example.demochatapplication.theme.DarkMessageCardBackgroundSender
import com.example.demochatapplication.theme.Typography
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.absoluteValue

@Composable
fun ChatMessageCard(
    modifier: Modifier = Modifier,
    chatModel: ChatScreenUiModel.ChatModel,
    cornerRoundnessDpValues: CornerRoundnessDpValues = CornerRoundnessDpValues().getSimpleRoundedCornerValues(),
    senderBackgroundColor: Color = MaterialTheme.colors.onBackground,
    receiverBackgroundColor: Color = MaterialTheme.colors.primary,
    username: String,
) {

    val timeCreatedMillis = remember { chatModel.timeInMillis }
    val calendar = remember {
        Calendar.getInstance().apply {
            timeInMillis = timeCreatedMillis
        }
    }

    val simpleDateFormat = remember {
        SimpleDateFormat("E, MMM dd, YYYY  HH:mm")
    }

    val maxWidth = LocalConfiguration.current.screenWidthDp
    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .wrapContentHeight()
                .align(if (username == chatModel.from) Alignment.CenterEnd else Alignment.CenterStart),
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
                    .padding(PaddingValues.SMALL),
                horizontalAlignment = if (chatModel.from == username) Alignment.End else Alignment.Start,
            ) {
                Text(
                    text = chatModel.message,
                    style = Typography.body1,
                    color = if (chatModel.from == username) Color.White else Color.DarkGray,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .wrapContentWidth()
                        .widthIn(min = PaddingValues.VERY_SMALL, (maxWidth.absoluteValue * 0.75).dp)
                )

                Spacer(modifier = Modifier.height(PaddingValues.SMALL))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Text(
                        text = simpleDateFormat.format(calendar.time),
                        color = Color.Unspecified.copy(alpha = 0.6f),
                        style = Typography.body2.copy(fontSize = MaterialTheme.typography.caption.fontSize),
                    )

                    if (chatModel.from == username) {
                        Spacer(modifier = Modifier.width(PaddingValues.SMALL))
                        Text(
                            text = chatModel.deliveryState.rawString,
                            style = Typography.body2.copy(fontSize = MaterialTheme.typography.caption.fontSize),
                            modifier = Modifier,
                            textAlign = TextAlign.End,
                            color = Color.Unspecified.copy(alpha = 0.6f)
                        )
                    }
                }
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
        chatModel = ChatScreenUiModel.ChatModel(
            from = "def",
            to = "to",
            message = "this is a message",
            timeInMillis = System.currentTimeMillis()
        ),
        username = "def",
        senderBackgroundColor = Color.Yellow
    )
}