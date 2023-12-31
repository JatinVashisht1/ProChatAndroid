package com.example.demochatapplication.features.chat.ui.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class CornerRoundnessDpValues(
    val topStart: Dp = 0.dp,
    val topEnd: Dp = 0.dp,
    val bottomStart: Dp = 0.dp,
    val bottomEnd: Dp = 0.dp,
) {
    fun getSimpleRoundedCornerValues(): CornerRoundnessDpValues = CornerRoundnessDpValues(
        topStart = 10.dp,
        topEnd = 10.dp,
        bottomStart = 10.dp,
        bottomEnd = 10.dp
    )
}