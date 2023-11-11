package com.example.demochatapplication.features.destinationswitcher.ui.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp

@Composable
fun LoadingComposable(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "")
    val fontColor by transition.animateColor(
        initialValue = Color.Yellow,
        targetValue = Color.Transparent,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val scale by transition.animateFloat(
            initialValue = 1f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1000),
                repeatMode = RepeatMode.Reverse
            ), label = ""
        )

        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = "Checking credentials...",
                style = MaterialTheme
                    .typography.h5
                    .copy(fontFamily = FontFamily.Monospace, color = fontColor),
            )
            LoadingAnimation(
                modifier = Modifier
                    .size(192.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun LoadingAnimation(
    modifier: Modifier = Modifier,
    color: Color = Color(221, 44, 0, 255)
) {
    val infiniteTransition = rememberInfiniteTransition("")
    val scaleDotOne by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, 50),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val scaleDotTwo by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, 150),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    val scaleDotThree by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, 250),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = color,
                radius = scaleDotOne.dp.toPx(),
                center = Offset(16.dp.toPx(), 24.dp.toPx()),
            )
            drawCircle(
                color = color,
                radius = scaleDotTwo.dp.toPx(),
                center = Offset(64.dp.toPx(), 24.dp.toPx())
            )
            drawCircle(
                color = color,
                radius = scaleDotThree.dp.toPx(),
                center = Offset(112.dp.toPx(), 24.dp.toPx())
            )
        }
    }
}
